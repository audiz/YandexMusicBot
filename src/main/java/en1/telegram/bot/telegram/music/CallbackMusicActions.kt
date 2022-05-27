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
    fun artistWithPagesMsg(chatId: String, callback: ArtistTrackWithPagesCallback): ResultOf<SendMessage>
    /**
     * Show search information with pagination and handle page numbers
     * */
    fun searchWithPagesMsg(chatId: String, callback: SearchTrackWithPagesCallback): ResultOf<SendMessage>
    /**
     * Show similar artists
     * */
    fun similarMsg(chatId: String, callback: SimilarCallback): ResultOf<SendMessage>
    /**
     * Send mp3 track to user
     * */
    fun document(update: Update, callback: TrackCallback): ResultOf<SendDocument>
}