package en1.telegram.bot.telegram.callback

import en1.telegram.bot.telegram.callback.dto.Callbacks

/**
 * Telegram callback message generation and parser
 * */
interface CallbackTypes {
    /**
     * Generate track callback
     * */
    fun trackCallback(track: Int, artist: Int, search: String): String
    fun searchTracksWithPagesCallback(page: Int, search: String): String
    fun artistTracksWithPagesCallback(page: Int, artistId: Int, search: String): String
    fun similarCallback(artistId: Int): String
    /**
     * Parse [callback] string to object
     * */
    fun parseCallback(callback: String): Callbacks
    /**
     * Parse [callback] for needed [clazz] type
     * */
    fun <T> parseCallback(callback: String, clazz: Class<T>): T?
}