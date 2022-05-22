package en1.telegram.fit_to_gpx_bot.telegram.callback

import en1.telegram.fit_to_gpx_bot.telegram.callback.dto.ArtistCallback
import en1.telegram.fit_to_gpx_bot.telegram.callback.dto.PagerTrackArtistCallback
import en1.telegram.fit_to_gpx_bot.telegram.callback.dto.PagerTrackSearchCallback
import en1.telegram.fit_to_gpx_bot.telegram.callback.dto.TrackCallback
import org.springframework.stereotype.Component

private const val API_CALLBACK_LENGTH_LIMIT = 62

@Component
class CallbackTypesImpl : CallbackTypes {
    companion object {
        const val TRACK_CALLBACK_IDENTIFIER = "tci"
        const val ARTIST_CALLBACK_IDENTIFIER = "aci"
        const val PAGER_TRACK_SEARCH_CALLBACK_IDENTIFIER = "pts"
        const val PAGER_TRACK_ARTIST_CALLBACK_IDENTIFIER = "pta"
    }

    override fun generateTrackCallback(track: Int, artist: Int, search: String): String {
        var callback = "$TRACK_CALLBACK_IDENTIFIER;$track;$artist;$search"
        if (callback.length > API_CALLBACK_LENGTH_LIMIT) {
            callback = "$TRACK_CALLBACK_IDENTIFIER;$track;$artist"
        }
        return callback
    }

    override fun generateArtistCallback(id: Int, name: String, search: String): String {
        var callback = "$ARTIST_CALLBACK_IDENTIFIER;$id;$name;$search"
        if (callback.length > API_CALLBACK_LENGTH_LIMIT) {
            callback = "$ARTIST_CALLBACK_IDENTIFIER;$id;$name"
        }
        return callback
    }

    override fun generateTrackSearchPagerCallback(page: Int, search: String): String {
        return "$PAGER_TRACK_SEARCH_CALLBACK_IDENTIFIER;$page;$search"
    }

    override fun generateTrackArtistPagerCallback(page: Int, artistId: Int, search: String): String {
        return "$PAGER_TRACK_ARTIST_CALLBACK_IDENTIFIER;$page;$artistId;$search"
    }

    override fun parseCallback(callback: String): Any? {
        val callbackId = callback.substringBefore(";")
        if (TRACK_CALLBACK_IDENTIFIER == callbackId) {
            val tracks = callback.substringAfter(";").split(";")
            return TrackCallback(tracks[0].toInt(), tracks[1].toInt(), tracks[2])
        }
        else if (ARTIST_CALLBACK_IDENTIFIER == callbackId) {
            val artist = callback.substringAfter(";").split(";")
            return ArtistCallback(artist[0].toInt(), artist[1], artist[2])
        }
        else if (PAGER_TRACK_SEARCH_CALLBACK_IDENTIFIER == callbackId) {
            val pagerTrackSearch = callback.substringAfter(";").split(";")
            return PagerTrackSearchCallback(pagerTrackSearch[0].toInt(), pagerTrackSearch[1])
        }
        else if (PAGER_TRACK_ARTIST_CALLBACK_IDENTIFIER == callbackId) {
            val pagerTrackArtist = callback.substringAfter(";").split(";")
            return PagerTrackArtistCallback(pagerTrackArtist[0].toInt(), pagerTrackArtist[1].toInt(), pagerTrackArtist[2])
        }
        else {
            return null
        }
    }

    override fun <T> parseCallback(callback: String, clazz: Class<T>): T? {
        val obj = parseCallback(callback) ?: return null
        if (obj::class.java == clazz) {
            return obj as T
        }
        return null
    }
}