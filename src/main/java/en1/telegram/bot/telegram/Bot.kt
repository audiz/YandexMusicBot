package en1.telegram.bot.telegram

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import en1.common.ERROR_UNKNOWN_CALLBACK
import en1.common.ResultOf
import en1.telegram.bot.telegram.callback.*
import en1.telegram.bot.telegram.commands.service.DailyPlaylistCommand
import en1.telegram.bot.telegram.commands.service.HelpCommand
import en1.telegram.bot.telegram.commands.service.StartCommand
import en1.telegram.bot.telegram.music.CallbackMusicActions
import en1.telegram.bot.telegram.nonCommand.NonCommand
import en1.telegram.bot.telegram.service.CaptchaService
import en1.telegram.bot.telegram.service.FitToGpxConverter
import en1.telegram.bot.telegram.service.ResultMessageSender
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import java.io.InputStream


@Component
class Bot(val fitToGpxConverter: FitToGpxConverter, private val musicService: CallbackMusicActions, private val captchaService: CaptchaService,
          private val resultMessageSender: ResultMessageSender): TelegramLongPollingCommandBot() {
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
            val userId = update.callbackQuery.from.id
            val chatId = update.callbackQuery.message.chatId.toString()
            val callback = update.callbackQuery.data.decodeCallback()
            val keyboardList = update.callbackQuery.message.replyMarkup.keyboard
            if (allowedUsers.contains(userId.toString())) {
                processCallback(callback, userId, chatId, keyboardList)
            } else {
                sendCommandUnknown(chatId)
            }
        } else {
            val msg: Message = update.message ?: update.editedMessage
            val userId = msg.from.id
            val chatId = msg.chatId.toString()

            if (msg.hasDocument() && msg.document.fileName.lowercase().endsWith(".fit")) {
                sendFitDoc(msg, update)
            } else {
                processStringMsg(userId, chatId, msg.text)
            }
        }
    }

    /**
     * Process direct user input to search tracks or answer captcha.
     * */
    private fun processStringMsg(userId: Long, chatId: String, text: String) {
        try {
            if (allowedUsers.contains(userId.toString())) {
                val result = if (captchaService.containsKey(userId)) {
                    logger.info("Process captcha msg = {}", text)
                    val captchaResult = captchaService.get(userId)
                    captchaService.remove(userId)
                    musicService.answerCaptcha(chatId, text, captchaResult)
                } else {
                    musicService.searchByString(chatId, text)
                }

                resultMessageSender.sendMessageAnswer(userId, chatId, result, this)
            } else {
                sendCommandUnknown(chatId)
            }
        } catch (e: Exception) {
            logger.error("processStringMsg exception: {}", e.stackTraceToString())
            sendInternalError(chatId)
        }
    }

    /**
     * Answer on different callback types for user
     * */
    private fun processCallback(callback: Callbacks, userId: Long, chatId: String, keyboardList: List<List<InlineKeyboardButton>>? = null) {
        val callbackResult = when (callback) {
            is TrackCallback -> musicService.document(userId, chatId, callback, keyboardList!!)
            is SearchTrackWithPagesCallback -> musicService.searchWithPagesMsg(userId, chatId, callback)
            is ArtistTrackWithPagesCallback -> musicService.artistWithPagesMsg(userId, chatId, callback)
            is SimilarCallback -> musicService.similarMsg(userId, chatId, callback)
            is UnknownCallback -> ResultOf.Failure("None callback", ERROR_UNKNOWN_CALLBACK)
        }
        resultMessageSender.sendMessage(userId, chatId, callbackResult, this)
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
            logger.error("sendFitDoc exception: {}", e.stackTraceToString())
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
            logger.error("sendCommandUnknown exception: {}", e.stackTraceToString())
        }
    }

    /**
     * Send internal error
     * */
    private fun sendInternalError(chatId: String) {
        try {
            val sendMessage = SendMessage()
            sendMessage.chatId = chatId
            sendMessage.text = "Internal error while processing"
            execute(sendMessage)
        } catch (e: Exception) {
            logger.error("sendInternalError exception: {}", e.stackTraceToString())
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
        register(DailyPlaylistCommand("daily", "Daily playlist", musicService, resultMessageSender))
        logger.info("Бот создан!")
    }
}