package bot.telegram.callback

import bot.common.ResultOf
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

/**
 * Actions for users callbacks from telegram
 * */
interface PlaylistActions {

    fun cancelDownloadPlaylist(userId: Long, chatId: String): ResultOf<SendMessage>

    fun showDownloadPlaylist(userId: Long, chatId: String): ResultOf<SendMessage>

    /**
     * Show personal playlists for yandex user
     * */
    fun getPersonalPlaylists(chatId: String): ResultOf<SendMessage>

    /**
     * Return a daily playlist for the current yandex music
     * */
    fun dailyPlaylist(userId: Long, chatId: String): ResultOf<SendMessage>

    /**
     * All playlists
     * */
    fun playlist(userId: Long, chatId: String, callback: PlaylistCallback): ResultOf<SendMessage>

    /**
     * Find music information for search request and show to user
     * */
    fun searchByString(userId: Long, chatId: String, text: String): ResultOf<SendMessage>
    /**
     * Show artist tracks to the user
     * */
    fun artistWithPagesMsg(userId: Long, chatId: String, callback: ArtistTrackWithPagesCallback): ResultOf<SendMessage>
    /**
     * Show search information with pagination and handle page numbers
     * */
    fun searchWithPagesMsg(userId: Long, chatId: String, callback: SearchTrackWithPagesCallback): ResultOf<SendMessage>
    /**
     * Show similar artists
     * */
    fun similarMsg(userId: Long, chatId: String, callback: SimilarCallback): ResultOf<SendMessage>

    /**
     * Answer captcha
     * */
    fun answerCaptcha(chatId: String, answer: String, captcha: ResultOf.Captcha): ResultOf<SendMessage>
}