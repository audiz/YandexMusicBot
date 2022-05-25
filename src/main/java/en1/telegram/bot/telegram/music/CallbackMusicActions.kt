package en1.telegram.bot.telegram.music

import en1.telegram.bot.telegram.callback.dto.*
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
    fun introMsg(msg: Message): SendMessage
    /**
     * Show artist tracks to the user
     * */
    fun artistWithPagesMsg(update: Update, callback: ArtistTrackWithPagesCallback): SendMessage
    /**
     * Show search information with pagination and handle page numbers
     * */
    fun searchWithPagesMsg(update: Update, callback: SearchTrackWithPagesCallback): SendMessage
    /**
     * Show similar artists
     * */
    fun similarMsg(update: Update, callback: SimilarCallback): SendMessage
    /**
     * Send mp3 track to user
     * */
    fun document(update: Update, callback: TrackCallback): SendDocument
}