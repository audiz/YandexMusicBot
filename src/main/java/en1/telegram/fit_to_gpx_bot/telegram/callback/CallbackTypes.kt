package en1.telegram.fit_to_gpx_bot.telegram.callback

interface CallbackTypes {

    fun trackCallback(track: Int, artist: Int, search: String): String
    fun artistCallback(id: Int, name: String, search: String): String
    fun searchTracksWithPagesCallback(page: Int, search: String): String
    fun artistTracksWithPagesCallback(page: Int, artistId: Int, search: String): String
    fun similarCallback(artistId: Int): String
    fun parseCallback(callback: String): Any?
    fun <T> parseCallback(callback: String, clazz: Class<T>): T?
}