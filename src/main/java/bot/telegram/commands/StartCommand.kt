package bot.telegram.commands

import bot.common.Utils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

/**
 * Команда "Старт"
 */
@Component
class StartCommand : ServiceCommand("start", "Начало") {
    private val logger = LoggerFactory.getLogger(StartCommand::class.java)
    override fun execute(absSender: AbsSender, user: User, chat: Chat, strings: Array<String>) {
        val userName: String = Utils.getUserName(user)
        logger.info("Пользователь {} ({}). Начато выполнение команды {}", userName, user.id, commandIdentifier)
        sendAnswer(absSender, chat.id, commandIdentifier, userName,
                """
                    Hello, I'am a bot that can find and download mp3 from yandex music
                  
                    ❗*List of the commands*
                    /test - test command
                    /help - help information
                    
                    Good Luck🙂
                    """.trimIndent())
        logger.debug(String.format("Пользователь %s. Завершено выполнение команды %s", userName, commandIdentifier))
    }
}