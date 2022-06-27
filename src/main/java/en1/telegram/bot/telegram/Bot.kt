package en1.telegram.bot.telegram

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import en1.common.ERROR_UNKNOWN_CALLBACK
import en1.common.ResultOf
import en1.telegram.bot.telegram.callback.*
import en1.telegram.bot.telegram.commands.service.HelpCommand
import en1.telegram.bot.telegram.commands.service.StartCommand
import en1.telegram.bot.telegram.music.CallbackMusicActions
import en1.telegram.bot.telegram.nonCommand.NonCommand
import en1.telegram.bot.telegram.service.CaptchaService
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import java.io.InputStream


@Component
class Bot(val fitToGpxConverter: FitToGpxConverter, val musicService: CallbackMusicActions, val captchaService: CaptchaService): TelegramLongPollingCommandBot() {
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

    private fun processStringMsg(userId: Int, chatId: String, text: String) {
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
                // Show answer to user
                when (result) {
                    is ResultOf.Success -> execute(result.value)
                    is ResultOf.Failure -> sendCommandFailure(chatId, "Failure ${result.code}")
                    is ResultOf.Captcha -> sendCommandFailure(chatId, "Failure captcha")
                }
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
    private fun processCallback(callback: Callbacks, userId: Int, chatId: String, keyboardList: List<List<InlineKeyboardButton>>? = null) {

        val callbackResult = when (callback) {
            is TrackCallback -> musicService.document(userId, chatId, callback, keyboardList!!)
            is SearchTrackWithPagesCallback -> musicService.searchWithPagesMsg(userId, chatId, callback)
            is ArtistTrackWithPagesCallback -> musicService.artistWithPagesMsg(userId, chatId, callback)
            is SimilarCallback -> musicService.similarMsg(userId, chatId, callback)
            is UnknownCallback -> ResultOf.Failure("None callback", ERROR_UNKNOWN_CALLBACK)
        }

        when (callbackResult) {
            is ResultOf.Success -> when (callbackResult.value) {
                is SendMessage -> execute(callbackResult.value)
                is SendDocument -> execute(callbackResult.value)
            }
            is ResultOf.Captcha -> showCaptcha(userId, chatId, callbackResult)
            is ResultOf.Failure -> {
                logger.error("Failure msg = ${callbackResult.message}, code = ${callbackResult.code}")
                sendCommandFailure(chatId, "Bot failed with code '${callbackResult.code}'")
            }
        }
    }

    /**
     * Send command is unknown
     * */
    private fun showCaptcha(userId: Int, chatId: String, captcha: ResultOf.Captcha) {
        try {
            captchaService.put(userId, captcha)
            logger.info("captchaService.containsKey(userId) = {}", captchaService.containsKey(userId))

            val sendMessage = SendMessage()
            sendMessage.chatId = chatId
            sendMessage.text = "Captcha img path = ${captcha.captcha.imgUrl}"
            execute(sendMessage)
        } catch (e: Exception) {
            logger.error("showCaptcha exception: {}", e.stackTraceToString())
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
            logger.error("sendCommandFailure exception: {}", e.stackTraceToString())
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