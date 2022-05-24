package en1.telegram.fit_to_gpx_bot.telegram

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import en1.telegram.fit_to_gpx_bot.telegram.callback.CallbackTypes
import en1.telegram.fit_to_gpx_bot.telegram.callback.dto.*
import en1.telegram.fit_to_gpx_bot.telegram.commands.service.HelpCommand
import en1.telegram.fit_to_gpx_bot.telegram.music.CallbackMusicActions
import en1.telegram.fit_to_gpx_bot.telegram.nonCommand.NonCommand
import en1.telegram.fit_to_gpx_bot.telegram.service.FitToGpxConverter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.objects.File
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import java.io.InputStream


@Component
class Bot(val fitToGpxConverter: FitToGpxConverter, val callbackTypes: CallbackTypes, val musicService: CallbackMusicActions): TelegramLongPollingCommandBot() {
    private val logger = LoggerFactory.getLogger(Bot::class.java)
    private val botUsername: String

    override fun getBotUsername(): String {
        return botUsername
    }

    private val botToken: String
    override fun getBotToken(): String {
        return botToken
    }

    private val nonCommand: NonCommand

    /**
     * Ответ на запрос, не являющийся командой
     */
    override fun processNonCommandUpdate(update: Update) {
        if (update.hasCallbackQuery()) {
            processCallback(update)
        } else {
            val msg: Message = update.message
            if (msg.hasDocument() && msg.document.fileName.lowercase().endsWith(".fit")) {
                sendFitDoc(msg, update)
            } else {
                execute(musicService.introMsg(msg))
            }
        }

    }

    /**
     * Answer on different callback types for user
     * */
    private fun processCallback(update: Update) {
        when (val callback = callbackTypes.parseCallback(update.callbackQuery.data)) {
            is TrackCallback -> execute(musicService.document(update, callback))
            is ArtistCallback -> execute(musicService.artistMsg(update, callback))
            is SearchTrackWithPagesCallback -> execute(musicService.searchTrackWithPagesMsg(update, callback))
            is ArtistTrackWithPagesCallback -> execute(musicService.artistWithPagesMsg(update, callback))
            is SimilarCallback -> execute(musicService.similarMsg(update, callback))
            else -> logger.warn("Unknown callback")
        }

    }

    /**
     * Transform garmin FIT file to GPX and send to user
     * */
    private fun sendFitDoc(msg: Message, update: Update) {
        logger.info("msg.getDocument().getFileName() = {}", msg.document.fileName)
        val docId: String = update.message.document.fileId
        val docName: String = update.message.document.fileName
        val chatId: Long = msg.chatId
        val getFile = GetFile()
        getFile.fileId = docId
        try {
            val file: File = execute(getFile)
            val `is`: InputStream = downloadFileAsStream(file)
            val stream: InputStream = fitToGpxConverter.decode(`is`)
            val sendDocument = SendDocument()
            sendDocument.chatId = chatId.toString()
            sendDocument.document = InputFile(stream, String.format("%s.gpx", docName.substringBeforeLast(".")))
            execute(sendDocument)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private val mapper = jacksonObjectMapper()
        init {
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
    }

    init {
        val getenv = System.getenv()
        logger.debug("Конструктор суперкласса отработал")
        botUsername = getenv!!["BOT_NAME"]!!
        this.botToken = getenv["BOT_TOKEN"]!!
        logger.debug("Имя и токен присвоены")
        nonCommand = NonCommand()
        logger.debug("Класс обработки сообщения, не являющегося командой, создан")
        register(HelpCommand("help", "Помощь"))
        logger.debug("Команда help создана")
        logger.info("Бот создан!")
    }
}