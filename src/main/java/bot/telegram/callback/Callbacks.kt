package bot.telegram.callback

import bot.common.API_CALLBACK_LENGTH_LIMIT
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

/**
 * Possible callback types for music search
 * */
sealed interface Callbacks

sealed class Callback : Callbacks {
    abstract fun encode(): String
}

/**
 * Track name button was clicked
 * */
data class TrackCallback(val trackId: Int, val artistId: Int, val searchString: String): Callback() {
    companion object {
        const val identifier: Byte = 1
    }

    override fun encode(): String {
        return generateStringFromData(identifier, intArrayOf(trackId, artistId), null, searchString)
    }
}

/**
 * Page number was clicked for search string tracks
 * */
data class SearchTrackWithPagesCallback(val page: Int, val searchString: String) : Callback() {
    companion object {
        const val identifier: Byte = 3
    }

    override fun encode(): String {
        return generateStringFromData(identifier, intArrayOf(page), null, searchString)
    }
}

/**
 * Page number was clicked for artists tracks
 * */
data class ArtistTrackWithPagesCallback(val page: Int, val artistId: Int, val searchString: String) : Callback() {
    companion object {
        const val identifier: Byte = 4
    }

    override fun encode(): String {
        return generateStringFromData(identifier, intArrayOf(page, artistId), null, searchString)
    }
}

/**
 * Similar button was clicked
 * */
data class SimilarCallback(val artistId: Int) : Callback() {
    companion object {
        const val identifier: Byte = 5
    }

    override fun encode(): String {
        return generateStringFromData(identifier, intArrayOf(artistId), null, null)
    }
}

/**
 * Playlist
 * */
data class PlaylistCallback(val kind: Long, val owner: String): Callback() {
    companion object {
        const val identifier: Byte = 6
    }

    override fun encode(): String {
        return generateStringFromData(identifier, null, "$kind:$owner", null)
    }
}

/**
 * Download Playlist
 * */
data class DownloadPlaylistCallback(val userId: Long, val trackFrom: Int, val trackTo: Int, val position: Int): Callback() {
    companion object {
        const val identifier: Byte = 7
    }

    override fun encode(): String {
        return generateStringFromData(identifier, intArrayOf(trackFrom, trackTo, position), "$userId", null)
    }
}

/**
 * Cancel Download Playlist
 * */
data class CancelDownloadPlaylistCallback(val userId: Long): Callback() {
    companion object {
        const val identifier: Byte = 8
    }

    override fun encode(): String {
        return generateStringFromData(identifier, null, "$userId", null)
    }
}

object UnknownCallback: Callbacks

/**
 * Create byte representation for callback with integers array and strings
 * */
fun generateStringFromData(callbackId: Byte, intArray: IntArray?, text: String?, search: String?): String {
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

    //if (outputStream.size() > API_CALLBACK_LENGTH_LIMIT) {
        //logger.warn("outputStream.size() > API_CALLBACK_LENGTH_LIMIT: {} > {}", outputStream.size(), API_CALLBACK_LENGTH_LIMIT)
    //}

    val data = outputStream.toByteArray()
    return String(data, Charsets.ISO_8859_1)
}

fun String.decodeCallback() : Callbacks {
    val bytesCoded = this.toByteArray(Charsets.ISO_8859_1)
    val callbackId = bytesCoded[0]
    val intSize = bytesCoded[1]
    val intBytes = bytesCoded.copyOfRange(2, intSize.toInt() + 2)
    val intArr = IntByteArraysConverter.convert(intBytes)
    val rawString = String(bytesCoded.copyOfRange(intSize.toInt() + 2, bytesCoded.size), Charsets.ISO_8859_1)
    val textData = rawString.substringBeforeLast(";")
    val searchString = rawString.substringAfterLast(";")

    return when (callbackId) {
        TrackCallback.identifier -> TrackCallback(intArr[0], intArr[1], searchString)
        SearchTrackWithPagesCallback.identifier -> SearchTrackWithPagesCallback(intArr[0], searchString)
        ArtistTrackWithPagesCallback.identifier -> ArtistTrackWithPagesCallback(intArr[0], intArr[1], searchString)
        SimilarCallback.identifier -> SimilarCallback(intArr[0])
        PlaylistCallback.identifier -> PlaylistCallback(textData.substringBefore(":").toLong(), textData.substringAfter(":"))
        DownloadPlaylistCallback.identifier -> DownloadPlaylistCallback(textData.toLong(), intArr[0], intArr[1], intArr[2])
        CancelDownloadPlaylistCallback.identifier -> CancelDownloadPlaylistCallback(textData.toLong())
        else -> UnknownCallback
    }
}

@SuppressWarnings("unchecked")
fun <T> String.decodeCallback(clazz: Class<T>): T? {
    val obj = this.decodeCallback()
    if (obj::class.java == clazz) {
        return obj as T
    }
    return null
}