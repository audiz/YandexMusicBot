package en1.telegram.bot.telegram.commands.service

import en1.common.ResultOf
import en1.telegram.bot.telegram.music.CallbackMusicActions
import en1.telegram.bot.telegram.service.CaptchaService
import en1.telegram.bot.telegram.service.ResultMessageSender
import en1.telegram.bot.utils.Utils
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

/**
 * Daily playlist command
 */
class DailyPlaylistCommand(identifier: String?, description: String?, private val musicService: CallbackMusicActions,
                           private val resultMessageSender: ResultMessageSender) : BotCommand(identifier, description) {
    private val logger = LoggerFactory.getLogger(DailyPlaylistCommand::class.java)

    override fun execute(absSender: AbsSender, user: User, chat: Chat, strings: Array<String>) {
        val userName: String = Utils.getUserName(user)
        val userId = user.id
        val chatId = chat.id.toString()
        logger.info("Пользователь {} ({}). Начато выполнение команды {}", userName, user.id, commandIdentifier)
        val callbackResult = musicService.dailyPlaylist(chat.id.toString())
        resultMessageSender.sendMessage(userId, chatId, callbackResult, absSender)

        logger.debug(String.format("Пользователь %s. Завершено выполнение команды %s", userName, commandIdentifier))
    }
}