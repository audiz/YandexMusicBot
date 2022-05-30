package en1.telegram.bot.telegram.music

import en1.common.ResultOf
import en1.telegram.bot.telegram.callback.ArtistTrackWithPagesCallback
import en1.telegram.bot.telegram.callback.SearchTrackWithPagesCallback
import en1.telegram.bot.telegram.callback.SimilarCallback
import en1.telegram.bot.telegram.callback.TrackCallback
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

/**
 * Actions for users callbacks from telegram
 * */
interface CallbackMusicActions {
    /**
     * Find music information for search request and show to user
     * */
    fun introMsg(msg: Message): ResultOf<SendMessage>
    /**
     * Show artist tracks to the user
     * */
    fun artistWithPagesMsg(userId: Int, chatId: String, callback: ArtistTrackWithPagesCallback): ResultOf<SendMessage>
    /**
     * Show search information with pagination and handle page numbers
     * */
    fun searchWithPagesMsg(userId: Int,chatId: String, callback: SearchTrackWithPagesCallback): ResultOf<SendMessage>
    /**
     * Show similar artists
     * */
    fun similarMsg(userId: Int,chatId: String, callback: SimilarCallback): ResultOf<SendMessage>
    /**
     * Send mp3 track to user
     * */
    fun document(userId: Int,chatId: String, callback: TrackCallback, keyboardList: List<List<InlineKeyboardButton>>): ResultOf<SendDocument>
}