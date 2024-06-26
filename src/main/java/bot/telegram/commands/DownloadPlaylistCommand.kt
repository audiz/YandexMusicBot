package bot.telegram.commands

import bot.telegram.callback.PlaylistActions
import bot.telegram.service.MessageSender
import bot.common.Utils
import bot.telegram.repository.UserStorage
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import java.util.*

/**
 * Daily playlist command
 */
@Component
class DownloadPlaylistCommand(private val playlistActions: PlaylistActions,
                              private val messageSender: MessageSender,
                              private val userStorage: UserStorage,
                              val messageSource: MessageSource,
) : BotCommand("download", "Download the latest playlist") {
    private val logger = LoggerFactory.getLogger(DownloadPlaylistCommand::class.java)

    override fun execute(absSender: AbsSender, user: User, chat: Chat, strings: Array<String>) {
        val userName: String = Utils.getUserName(user)
        val userId = user.id
        val chatId = chat.id.toString()
        logger.info("Пользователь {} ({}). Начато выполнение команды {}", userName, user.id, commandIdentifier)
        val storage = userStorage.getUserStorageData(userId)
        val inProgress: Boolean = !((storage?.loaded?.find{ it == false }) ?: true)

        if (storage == null || storage.tracks.isEmpty()) {
            messageSender.sendSimpleMsg(chatId, absSender, messageSource.getMessage("download.latest.playlist.not.found", null, Locale.getDefault()))
        } else if (inProgress) {
            messageSender.sendMessage(userId, chatId, playlistActions.cancelDownloadPlaylist(userId, chatId), absSender)
        } else {
            messageSender.sendMessage(userId, chatId, playlistActions.showDownloadPlaylist(userId, chatId), absSender)
        }
        logger.debug("Пользователь {}. Завершено выполнение команды {}", userName, commandIdentifier)
    }
}