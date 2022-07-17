package en1.telegram.bot.telegram.service

import en1.common.ResultOf
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.bots.AbsSender

/**
 * Send different message types for users request
 * */
@Component
class ResultMessageSenderImpl(private val captchaService: CaptchaService) : ResultMessageSender {
    private val logger = LoggerFactory.getLogger(ResultMessageSenderImpl::class.java)

    /**
     * Send message to user on callback
     * */
    override fun sendMessage(userId: Long, chatId: String, result: ResultOf<Any>, absSender: AbsSender) {
        when (result) {
            is ResultOf.Success -> when (result.value) {
                is SendMessage -> absSender.execute(result.value)
                is SendDocument -> absSender.execute(result.value)
            }
            is ResultOf.Captcha -> showCaptcha(absSender, userId, chatId, result)
            is ResultOf.Failure -> {
                logger.error("Failure msg = ${result.message}, code = ${result.code}")
                sendCommandFailure(absSender, chatId, "Bot failed with code '${result.code}'")
            }
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
}