package en1.telegram.bot.telegram.commands.service

import en1.telegram.bot.utils.Utils
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

/**
 * Команда "Старт"
 */
class StartCommand(identifier: String?, description: String?) : ServiceCommand(identifier, description) {
    private val logger = LoggerFactory.getLogger(StartCommand::class.java)
    override fun execute(absSender: AbsSender, user: User, chat: Chat, strings: Array<String>) {
        val userName: String = Utils.getUserName(user)
        logger.info("Пользователь {} ({}). Начато выполнение команды {}", userName, user.id, commandIdentifier)
        sendAnswer(absSender, chat.id, commandIdentifier, userName,
                """
                    Hello, I'am a bot that can convert a Garmin FIT file to GPX
                  
                    ❗*List of the commands*
                    /test - test command
                    /help - help information
                    
                    Good Luck🙂
                    """.trimIndent())
        logger.debug(String.format("Пользователь %s. Завершено выполнение команды %s", userName, commandIdentifier))
    }
}