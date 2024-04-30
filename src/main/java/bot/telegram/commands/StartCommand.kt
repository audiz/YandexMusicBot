package bot.telegram.commands

import bot.common.Utils
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import java.util.*

/**
 * Команда "Старт"
 */
@Component
class StartCommand(val messageSource: MessageSource) : ServiceCommand("start", "Начало") {
    private val logger = LoggerFactory.getLogger(StartCommand::class.java)
    override fun execute(absSender: AbsSender, user: User, chat: Chat, strings: Array<String>) {
        val userName: String = Utils.getUserName(user)
        logger.debug("Пользователь {} ({}). Начато выполнение команды {}", userName, user.id, commandIdentifier)
        sendAnswer(absSender, chat.id, commandIdentifier, userName, messageSource.getMessage("bot.command.start", null, Locale.getDefault()))
        logger.debug("Пользователь {} ({}). Завершено выполнение команды {}", userName, user.id, commandIdentifier)
    }
}