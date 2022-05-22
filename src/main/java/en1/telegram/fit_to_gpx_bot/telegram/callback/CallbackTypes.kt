package en1.telegram.fit_to_gpx_bot.telegram.callback

interface CallbackTypes {

    fun generateTrackCallback(track: Int, artist: Int, search: String): String
    fun generateArtistCallback(id: Int, name: String, search: String): String
    fun generateTrackSearchPagerCallback(page: Int, search: String): String
    fun generateTrackArtistPagerCallback(page: Int, artistId: Int, search: String): String

    fun parseCallback(callback: String): Any?
    fun <T> parseCallback(callback: String, clazz: Class<T>): T?
}