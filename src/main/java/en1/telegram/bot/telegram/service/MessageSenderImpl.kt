package en1.telegram.bot.telegram.service

import en1.common.ResultOf
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.File
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender
import java.io.InputStream
import java.net.URL

/**
 * Send different message types for users request
 * */
@Component
class MessageSenderImpl(private val captchaService: CaptchaService, val fitToGpxConverter: FitToGpxConverter) : MessageSender {
    private val logger = LoggerFactory.getLogger(MessageSenderImpl::class.java)

    /**
     * Send message or document to a user on the callback
     * */
    override fun sendMessage(userId: Long, chatId: String, result: ResultOf<Any>, absSender: AbsSender) {
        when (result) {
            is ResultOf.Success -> when (result.value) {
                is SendMessage -> absSender.execute(result.value)
                is SendDocument -> absSender.execute(result.value)
            }
            is ResultOf.Failure -> {
                logger.error("Failure msg = ${result.message}, code = ${result.code}")
                sendCommandFailure(absSender, chatId, "Bot failed with code '${result.code}'")
            }
            is ResultOf.Captcha -> showCaptcha(absSender, userId, chatId, result)
        }
    }

    /**
     * Show answer to users message
     * */
    override fun sendMessageAnswer(userId: Long, chatId: String, result: ResultOf<SendMessage>, absSender: AbsSender) {
        when (result) {
            is ResultOf.Success -> absSender.execute(result.value)
            is ResultOf.Failure -> sendCommandFailure(absSender, chatId, "Failure ${result.code}")
            is ResultOf.Captcha -> showCaptcha(absSender, userId, chatId, result)
        }
    }

    /**
     * Send captcha image to user: TODO send as file
     * */
    private fun showCaptcha(absSender: AbsSender, userId: Long, chatId: String, captcha: ResultOf.Captcha) {
        try {
            captchaService.put(userId, captcha)
            logger.info("captchaService.containsKey(userId) = {}", captchaService.containsKey(userId))

            val sendMessage = SendMessage()
            sendMessage.chatId = chatId
            sendMessage.text = "Captcha img path = ${captcha.captcha.imgUrl}"
            absSender.execute(sendMessage)

            val stream: InputStream = URL(captcha.captcha.imgUrl).openStream();
            val sendDocument = SendDocument()
            sendDocument.chatId = chatId
            sendDocument.document = InputFile(stream, "captcha.jpg")
            absSender.execute(sendDocument)

        } catch (e: Exception) {
            logger.error("showCaptcha exception: {}", e.stackTraceToString())
        }
    }

    /**
     * Send command is failed
     * */
    private fun sendCommandFailure(absSender: AbsSender, chatId: String, failureMsg: String) {
        try {
            val sendMessage = SendMessage()
            sendMessage.chatId = chatId
            sendMessage.text = failureMsg
            absSender.execute(sendMessage)
        } catch (e: Exception) {
            logger.error("sendCommandFailure exception: {}", e.stackTraceToString())
        }
    }

    /**
     * Send internal error
     * */
    override fun sendInternalError(chatId: String, absSender: AbsSender) {
        try {
            val sendMessage = SendMessage()
            sendMessage.chatId = chatId
            sendMessage.text = "Internal error while processing"
            absSender.execute(sendMessage)
        } catch (e: Exception) {
            logger.error("sendInternalError exception: {}", e.stackTraceToString())
        }
    }

    /**
     * Send command is unknown
     * */
    override fun sendCommandUnknown(chatId: String, absSender: AbsSender) {
        try {
            val sendMessage = SendMessage()
            sendMessage.chatId = chatId
            sendMessage.text = "Unknown command, try /help"
            absSender.execute(sendMessage)
        } catch (e: Exception) {
            logger.error("sendCommandUnknown exception: {}", e.stackTraceToString())
        }
    }

    /**
     * Transform garmin FIT file to GPX and send to user
     * */
    override fun sendFitDoc(msg: Message, update: Update, absSender: DefaultAbsSender) {
        logger.info("msg.getDocument().getFileName() = {}", msg.document.fileName)
        val docId: String = update.message.document.fileId
        val docName: String = update.message.document.fileName
        val chatId: Long = msg.chatId
        val getFile = GetFile()
        getFile.fileId = docId
        try {
            val file: File = absSender.execute(getFile)
            val `is`: InputStream = absSender.downloadFileAsStream(file)
            val stream: InputStream = fitToGpxConverter.decode(`is`)
            val sendDocument = SendDocument()
            sendDocument.chatId = chatId.toString()
            sendDocument.document = InputFile(stream, String.format("%s.gpx", docName.substringBeforeLast(".")))
            absSender.execute(sendDocument)
        } catch (e: Exception) {
            logger.error("sendFitDoc exception: {}", e.stackTraceToString())
        }
    }

    /**
     * Send command is locked by double execution
     * */
    override fun sendCommandLocked(chatId: String, absSender: DefaultAbsSender) {
        try {
            val sendMessage = SendMessage()
            sendMessage.chatId = chatId
            sendMessage.text = "The command is locked by double execution"
            absSender.execute(sendMessage)
        } catch (e: Exception) {
            logger.error("sendCommandUnknown exception: {}", e.stackTraceToString())
        }
    }
}