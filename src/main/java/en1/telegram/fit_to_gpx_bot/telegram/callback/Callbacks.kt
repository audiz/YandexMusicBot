package en1.telegram.fit_to_gpx_bot.telegram.callback.dto

/**
 * Possible callback types for music search
 * */
sealed interface Callbacks

sealed class Callback: Callbacks

/**
 * Track name button was clicked
 * */
data class TrackCallback(
    val trackId: Int,
    val artistId: Int,
    val searchString: String): Callback()

/**
 * Artist button was clicked
 * */
data class ArtistCallback(
    val id: Int,
    val name: String,
    val searchString: String
) : Callback()

/**
 * Page number was clicked for search string tracks
 * */
data class PagerTrackSearchCallback(
    val page: Int,
    val searchString: String
) : Callback()

/**
 * Page number was clicked for artists tracks
 * */
data class PagerTrackArtistCallback(
    val page: Int,
    val artistId: Int,
    val searchString: String
) : Callback()