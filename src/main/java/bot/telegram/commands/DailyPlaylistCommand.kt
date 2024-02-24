package bot.telegram.commands

import bot.telegram.callback.CallbackMusicActions
import bot.telegram.service.MessageSender
import bot.common.Utils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

/**
 * Daily playlist command
 */
@Component
class DailyPlaylistCommand(private val musicService: CallbackMusicActions,
                           private val messageSender: MessageSender
) : BotCommand("daily", "Daily playlist") {
    private val logger = LoggerFactory.getLogger(DailyPlaylistCommand::class.java)

    override fun execute(absSender: AbsSender, user: User, chat: Chat, strings: Array<String>) {
        val userName: String = Utils.getUserName(user)
        val userId = user.id
        val chatId = chat.id.toString()
        logger.info("Пользователь {} ({}). Начато выполнение команды {}", userName, user.id, commandIdentifier)
        val callbackResult = musicService.dailyPlaylist(chat.id.toString())
        messageSender.sendMessage(userId, chatId, callbackResult, absSender)
        logger.debug("Пользователь {}. Завершено выполнение команды {}", userName, commandIdentifier)
    }
}