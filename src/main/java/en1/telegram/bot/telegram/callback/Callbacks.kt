package en1.telegram.bot.telegram.callback.dto

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
 * Page number was clicked for search string tracks
 * */
data class SearchTrackWithPagesCallback(
    val page: Int,
    val searchString: String
) : Callback()

/**
 * Page number was clicked for artists tracks
 * */
data class ArtistTrackWithPagesCallback(
    val page: Int,
    val artistId: Int,
    val searchString: String
) : Callback()

/**
 * Similar button was clicked
 * */
data class SimilarCallback(
    val artistId: Int
) : Callback()

object UnknownCallback: Callbacks