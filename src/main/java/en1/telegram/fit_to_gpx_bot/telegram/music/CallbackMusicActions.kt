package en1.telegram.fit_to_gpx_bot.telegram.music

import en1.telegram.fit_to_gpx_bot.telegram.callback.dto.ArtistCallback
import en1.telegram.fit_to_gpx_bot.telegram.callback.dto.PagerTrackArtistCallback
import en1.telegram.fit_to_gpx_bot.telegram.callback.dto.PagerTrackSearchCallback
import en1.telegram.fit_to_gpx_bot.telegram.callback.dto.TrackCallback
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

interface CallbackMusicActions {
    fun sendSearchInfo(msg: Message): SendMessage
    fun processArtist(update: Update, callback: ArtistCallback): SendMessage
    fun processTrack(update: Update, callback: TrackCallback): SendDocument
    fun processPagerTrackSearch(update: Update, callback: PagerTrackSearchCallback): SendMessage
    fun processPagerArtistSearch(update: Update, callback: PagerTrackArtistCallback): SendMessage
}