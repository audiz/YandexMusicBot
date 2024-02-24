package bot.telegram.commands

import org.slf4j.LoggerFactory
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

/**
 * Суперкласс для сервисных команд
 */
abstract class ServiceCommand(identifier: String?, description: String?) : BotCommand(identifier, description) {
    private val logger = LoggerFactory.getLogger(ServiceCommand::class.java)

    /**
     * Отправка ответа пользователю
     */
    fun sendAnswer(absSender: AbsSender, chatId: Long, commandName: String?, userName: String?, text: String?) {
        val message = SendMessage()
        message.enableMarkdown(true)
        message.chatId = chatId.toString()
        message.text = text!!
        try {
            absSender.execute(message)
        } catch (e: TelegramApiException) {
            logger.error(String.format("Ошибка %s. Команда %s. Пользователь: %s", e.message, commandName, userName))
            e.printStackTrace()
        }
    }
}