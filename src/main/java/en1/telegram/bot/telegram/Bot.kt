package en1.telegram.bot.telegram

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import en1.common.ERROR_UNKNOWN_CALLBACK
import en1.common.ResultOf
import en1.telegram.bot.telegram.callback.CallbackTypes
import en1.telegram.bot.telegram.callback.dto.*
import en1.telegram.bot.telegram.commands.service.HelpCommand
import en1.telegram.bot.telegram.commands.service.StartCommand
import en1.telegram.bot.telegram.music.CallbackMusicActions
import en1.telegram.bot.telegram.nonCommand.NonCommand
import en1.telegram.bot.telegram.service.FitToGpxConverter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.File
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import java.io.InputStream


@Component
class Bot(val fitToGpxConverter: FitToGpxConverter, val callbackTypes: CallbackTypes, val musicService: CallbackMusicActions): TelegramLongPollingCommandBot() {
    private var allowedUsers: List<String> = listOf()
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
     * */
    override fun processNonCommandUpdate(update: Update) {
        if (update.hasCallbackQuery()) {
            if (allowedUsers.contains(update.callbackQuery.from.id.toString())) {
                processCallback(update)
            } else {
                sendCommandUnknown(update.callbackQuery.message.chatId.toString())
            }
        } else {
            val msg: Message = update.message
            if (msg.hasDocument() && msg.document.fileName.lowercase().endsWith(".fit")) {
                sendFitDoc(msg, update)
            } else {
                if (allowedUsers.contains(msg.from.id.toString())) {
                    val intro = musicService.introMsg(msg) as ResultOf.success
                    execute(intro.value)
                } else {
                    sendCommandUnknown(msg.chatId.toString())
                }
            }
        }
    }

    /**
     * Answer on different callback types for user
     * */
    private fun processCallback(update: Update) {

        val callbackResult = when (val callback = callbackTypes.parseCallback(update.callbackQuery.data)) {
            is TrackCallback -> musicService.document(update, callback)
            is SearchTrackWithPagesCallback -> musicService.searchWithPagesMsg(update, callback)
            is ArtistTrackWithPagesCallback -> musicService.artistWithPagesMsg(update, callback)
            is SimilarCallback -> musicService.similarMsg(update, callback)
            is UnknownCallback -> ResultOf.failure("None callback", ERROR_UNKNOWN_CALLBACK)
        }

        when (callbackResult) {
            is ResultOf.success -> {
                when (callbackResult.value) {
                    is SendMessage -> execute(callbackResult.value)
                    is SendDocument -> execute(callbackResult.value)
                 }
            }
            is ResultOf.failure -> {
                logger.error("Failure msg = ${callbackResult.message}, code = ${callbackResult.code}")
                sendCommandFailure(update.callbackQuery.message.chatId.toString(), "Bot failed with code '${callbackResult.code}'")
            }
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

    /**
     * Send command is unknown
     * */
    private fun sendCommandUnknown(chatId: String) {
        try {
            val sendMessage = SendMessage()
            sendMessage.chatId = chatId
            sendMessage.text = "Unknown command, try /help"
            execute(sendMessage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Send command is failed
     * */
    private fun sendCommandFailure(chatId: String, failureMsg: String) {
        try {
            val sendMessage = SendMessage()
            sendMessage.chatId = chatId
            sendMessage.text = failureMsg
            execute(sendMessage)
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
        this.botUsername = getenv!!["BOT_NAME"]!!
        this.botToken = getenv["BOT_TOKEN"]!!
        this.allowedUsers = getenv["ALLOWED_USERS"]?.split(",") ?: listOf()
        logger.debug("Имя и токен присвоены")
        nonCommand = NonCommand()

        register(StartCommand("start", "Начало"))
        register(HelpCommand("help", "Помощь"))
        logger.info("Бот создан!")
    }
}