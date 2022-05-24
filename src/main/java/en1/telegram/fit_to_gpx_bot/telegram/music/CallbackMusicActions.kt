package en1.telegram.fit_to_gpx_bot.telegram.music

import en1.telegram.fit_to_gpx_bot.telegram.callback.dto.*
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

interface CallbackMusicActions {
    fun introMsg(msg: Message): SendMessage
    fun artistMsg(update: Update, callback: ArtistCallback): SendMessage
    fun document(update: Update, callback: TrackCallback): SendDocument
    fun searchTrackWithPagesMsg(update: Update, callback: SearchTrackWithPagesCallback): SendMessage
    fun similarMsg(update: Update, callback: SimilarCallback): SendMessage
    fun artistWithPagesMsg(update: Update, callback: ArtistTrackWithPagesCallback): SendMessage

}