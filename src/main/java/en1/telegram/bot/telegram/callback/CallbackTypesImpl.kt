package en1.telegram.bot.telegram.callback

import en1.telegram.bot.telegram.callback.dto.*
import en1.telegram.bot.telegram.service.IntByteArraysConverter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

private const val API_CALLBACK_LENGTH_LIMIT = 62

@Component
class CallbackTypesImpl : CallbackTypes {
    companion object {
        private val logger = LoggerFactory.getLogger(CallbackTypesImpl::class.java)

        const val TRACK_CALLBACK_IDENTIFIER = 1.toByte()
        const val PAGER_TRACK_SEARCH_CALLBACK_IDENTIFIER = 3.toByte()
        const val PAGER_TRACK_ARTIST_CALLBACK_IDENTIFIER = 4.toByte()
        const val SIMILAR_ARTIST_CALLBACK_IDENTIFIER = 5.toByte()
    }

    override fun trackCallback(track: Int, artist: Int, search: String): String {
        return generateStringFromData(TRACK_CALLBACK_IDENTIFIER, intArrayOf(track, track), null, search)
    }

    override fun searchTracksWithPagesCallback(page: Int, search: String): String {
        return generateStringFromData(PAGER_TRACK_SEARCH_CALLBACK_IDENTIFIER, intArrayOf(page), null, search)
    }

    override fun artistTracksWithPagesCallback(page: Int, artistId: Int, search: String): String {
        return generateStringFromData(PAGER_TRACK_ARTIST_CALLBACK_IDENTIFIER, intArrayOf(page, artistId), null, search)
    }

    override fun similarCallback(artistId: Int): String {
        return generateStringFromData(SIMILAR_ARTIST_CALLBACK_IDENTIFIER, intArrayOf(artistId), null, null)
    }

    /**
     * Create callback object type from [callback] string
     * */
    override fun parseCallback(callback: String): Callbacks? {
        //val callbackId = callback.substringBefore(";")
        val bytesCoded = callback.toByteArray(Charsets.ISO_8859_1)
        val callbackId = bytesCoded[0]
        val intSize = bytesCoded[1]
        val intBytes = bytesCoded.copyOfRange(2, intSize.toInt() + 2)
        val intArr = IntByteArraysConverter.convert(intBytes)
        val rawString = String(bytesCoded.copyOfRange(intSize.toInt() + 2, bytesCoded.size), Charsets.ISO_8859_1)
        val textData = rawString.substringBeforeLast(";")
        val searchString = rawString.substringAfterLast(";")

        if (TRACK_CALLBACK_IDENTIFIER == callbackId) {
            return TrackCallback(intArr[0], intArr[1], searchString)
        }
        else if (PAGER_TRACK_SEARCH_CALLBACK_IDENTIFIER == callbackId) {
            return SearchTrackWithPagesCallback(intArr[0], searchString)
        }
        else if (PAGER_TRACK_ARTIST_CALLBACK_IDENTIFIER == callbackId) {
            return ArtistTrackWithPagesCallback(intArr[0], intArr[1], searchString)
        }
        else if (SIMILAR_ARTIST_CALLBACK_IDENTIFIER == callbackId) {
            return SimilarCallback(intArr[0])
        }
        else {
            logger.info("Unknown callback = {}", callback)
            return null
        }
    }

    /**
     * Return callback obj from string [callback] by [type]
     * */
    override fun <T> parseCallback(callback: String, clazz: Class<T>): T? {
        val obj = parseCallback(callback) ?: return null
        if (obj::class.java == clazz) {
            return obj as T
        }
        return null
    }

    /**
     * Create byte representation for callback with integers array and strings
     * */
    private fun generateStringFromData(callbackId: Byte, intArray: IntArray?, text: String?, search: String?): String {
        val outputStream = ByteArrayOutputStream()
        var intLen = 0
        var bytes = byteArrayOf()
        if (intArray != null) {
            bytes = IntByteArraysConverter.convert(intArray)
            intLen = bytes.size
        }
        val beginning = byteArrayOf(callbackId, intLen.toByte())
        var bytesStr = byteArrayOf()
        if (text != null) {
            bytesStr = text.toByteArray(StandardCharsets.ISO_8859_1)
        }
        outputStream.write(beginning)
        outputStream.write(bytes)
        outputStream.write(bytesStr)

        var searchStr = byteArrayOf()
        if (search != null) {
            val semicolon = if (text == null) { "" } else { ";" }
            searchStr =  (semicolon + search).toByteArray(StandardCharsets.ISO_8859_1)
        }
        if (outputStream.size() + searchStr.size <= API_CALLBACK_LENGTH_LIMIT) {
            outputStream.write(searchStr)
        }

        if (outputStream.size() > API_CALLBACK_LENGTH_LIMIT) {
            logger.warn("outputStream.size() > API_CALLBACK_LENGTH_LIMIT: {} > {}", outputStream.size(), API_CALLBACK_LENGTH_LIMIT)
        }

        val data = outputStream.toByteArray()
        return String(data, Charsets.ISO_8859_1)
    }
}