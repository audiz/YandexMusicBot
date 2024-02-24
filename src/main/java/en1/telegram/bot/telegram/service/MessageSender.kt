package en1.telegram.bot.telegram.service

import en1.common.ResultOf
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.bots.AbsSender

/**
 * Send different message types for users request
 * */
interface MessageSender {
    /**
     * Send message to user on callback
     * */
    fun sendMessage(userId: Long, chatId: String, result: ResultOf<Any>, absSender: AbsSender)

    /**
     * Show answer to users message
     * */
    fun sendMessageAnswer(userId: Long, chatId: String, result: ResultOf<SendMessage>, absSender: AbsSender)


    /**
     * Send internal error
     * */
    fun sendInternalError(chatId: String, absSender: AbsSender)

    /**
     * Send command is unknown
     * */
    fun sendCommandUnknown(chatId: String, absSender: AbsSender)

    fun sendNotAllowed(chatId: String, absSender: AbsSender)

    /**
     * Send command is locked by double execution
     * */
    fun sendCommandLocked(chatId: String, absSender: DefaultAbsSender)
}