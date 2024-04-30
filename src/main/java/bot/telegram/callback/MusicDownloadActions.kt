package bot.telegram.callback

import bot.common.ResultOf
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

interface MusicDownloadActions {

    fun cancelDownloadMp3List(userId: Long, chatId: String): ResultOf<SendMessage>

    /**
     * Send mp3 track to user
     * */
    fun downloadMp3List(userId: Long, chatId: String, callback: DownloadPlaylistCallback, absSender: DefaultAbsSender): ResultOf<SendMessage>

    /**
     * Send mp3 track to user
     * */
    fun downloadMp3(userId: Long, chatId: String, callback: TrackCallback, keyboardList: List<List<InlineKeyboardButton>>): ResultOf<SendDocument>
}