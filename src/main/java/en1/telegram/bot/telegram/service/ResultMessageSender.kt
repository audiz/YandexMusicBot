package en1.telegram.bot.telegram.service

import en1.common.ResultOf
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.bots.AbsSender

/**
 * Send different message types for users request
 * */
interface ResultMessageSender {
    /**
     * Send message to user on callback
     * */
    fun sendMessage(userId: Long, chatId: String, result: ResultOf<Any>, absSender: AbsSender)

    /**
     * Show answer to users message
     * */
    fun sendMessageAnswer(userId: Long, chatId: String, result: ResultOf<SendMessage>, absSender: AbsSender)
}