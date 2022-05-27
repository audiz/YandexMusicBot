package en1.telegram.bot.telegram.music

import en1.common.ResultOf
import en1.telegram.bot.telegram.callback.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import yandex.YandexMusic
import yandex.dto.domain.ArtistItem
import yandex.dto.domain.TrackItem
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

private const val ARTISTS_PER_PAGE = 10
private const val TRACKS_PER_PAGE = 10
private const val API_BUTTONS_ROW_LIMIT = 8

/**
 * Handler for callback user actions
 * */
@Component
class CallbackMusicActionsImpl(val yandexMusic: YandexMusic) : CallbackMusicActions {
    companion object {
        private val logger = LoggerFactory.getLogger(CallbackMusicActionsImpl::class.java)
    }

    /**
     * Show search tracks to user
     * */
    override fun introMsg(msg: Message): ResultOf<SendMessage> {

        // TODO cut string to limit
        val searchText = URLEncoder.encode(msg.text.trimIndent(), "utf-8")
        val searchResult = yandexMusic.search(searchText)
        if (searchResult is ResultOf.failure) {
            return searchResult
        }
        val search = (searchResult as ResultOf.success).value

        val rowList: MutableList<List<InlineKeyboardButton>> = ArrayList()

        createTrackButtons(search.tracks.items, rowList, searchText)
        rowList.add(pagesButtonsRow(search.tracks.total, -1, searchText))
        rowList.add(artistButtonsRow(search.artists.items, searchText))

        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        inlineKeyboardMarkup.keyboard = rowList

        val answer = SendMessage()
        answer.text = "Tracks found for request: ${search.tracks.total}"
        answer.chatId = msg.chatId.toString()
        answer.replyMarkup = inlineKeyboardMarkup

        return ResultOf.success(answer)
    }

    /**
     * Pagination for artists tracks with similar artists.
     * Limit for json, when more than 150 we should request by ids
     * */
    override fun artistWithPagesMsg(chatId: String, callback: ArtistTrackWithPagesCallback): ResultOf<SendMessage> {
        val page = callback.page
        val searchResult = yandexMusic.searchTrack(callback.artistId)
        if (searchResult is ResultOf.failure) {
            return searchResult
        }
        val artistSearch = (searchResult as ResultOf.success).value

        val rowList: MutableList<List<InlineKeyboardButton>> = ArrayList()

        createTrackButtons(artistSearch.tracks.drop((page - 1) * TRACKS_PER_PAGE).take(TRACKS_PER_PAGE), rowList, callback.searchString, artistSearch.artist.name)
        rowList.add(pagesButtonsRow(artistSearch.tracks.size, page, callback.searchString, callback.artistId))
        rowList.add(artistButtonsRow(artistSearch.similar.take(3), callback.searchString))
        if (artistSearch.similar.isNotEmpty()) {
            rowList.add(artistButtonShowMoreRow(callback.artistId))
        }

        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        inlineKeyboardMarkup.keyboard = rowList

        val answer = SendMessage()
        answer.text = "Artist page: $page (${(page * TRACKS_PER_PAGE)} of ${artistSearch.tracks.size} tracks)"
        answer.chatId = chatId
        answer.replyMarkup = inlineKeyboardMarkup

        return ResultOf.success(answer)
    }

    /**
     * Pagination for searched tracks
     * */
    override fun searchWithPagesMsg(chatId: String, callback: SearchTrackWithPagesCallback): ResultOf<SendMessage> {
        //logger.info("processPagerTrackSearch callback = {}", callback)
        val page = callback.page
        val realPageForRequest = ((page - 1) * TRACKS_PER_PAGE) / 100
        val searchResult = yandexMusic.searchTrack(callback.searchString, realPageForRequest)
        if (searchResult is ResultOf.failure) {
            return searchResult
        }
        val search = (searchResult as ResultOf.success).value


        val rowList: MutableList<List<InlineKeyboardButton>> = ArrayList()

        val total = if (search.tracks.total > 200) 200 else search.tracks.total

        createTrackButtons(search.tracks.items.drop(((page - 1) % TRACKS_PER_PAGE) * TRACKS_PER_PAGE).take(TRACKS_PER_PAGE), rowList, callback.searchString)
        rowList.add(pagesButtonsRow(total, page, callback.searchString))

        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        inlineKeyboardMarkup.keyboard = rowList

        val answer = SendMessage()
        answer.text = "Search page: $page (${(page * TRACKS_PER_PAGE)} of ${search.tracks.total} tracks)"
        answer.chatId = chatId
        answer.replyMarkup = inlineKeyboardMarkup

        return ResultOf.success(answer)
    }

    /**
     * Show similar artists
     * */
    override fun similarMsg(chatId: String, callback: SimilarCallback): ResultOf<SendMessage> {
        val searchResult = yandexMusic.getSimilar(callback.artistId)
        if (searchResult is ResultOf.failure) {
            return searchResult
        }
        if (searchResult is ResultOf.captcha) {
            // If captcha sow it and return back to this
            searchResult.callback = callback
            return searchResult
        }

        val artistSearch = (searchResult as ResultOf.success).value

        val rowList: MutableList<List<InlineKeyboardButton>> = ArrayList()

        artistSearch.allSimilar.take(3 * ARTISTS_PER_PAGE).chunked(3).map { rowList.add(artistButtonsRow(it, "")) }

        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        inlineKeyboardMarkup.keyboard = rowList

        val answer = SendMessage()
        answer.text = "Artists similar to ${artistSearch.artist.name}"
        answer.chatId = chatId
        answer.replyMarkup = inlineKeyboardMarkup
        return ResultOf.success(answer)
    }

    /**
     * Download track and send it to user
     * */
    override fun document(update: Update, callback: TrackCallback): ResultOf<SendDocument> {
        var songName = ""
        for (keyboard in update.callbackQuery.message.replyMarkup.keyboard) {
            for (inlineBtn in keyboard) {
                if (inlineBtn.callbackData.decodeCallback(TrackCallback::class.java)?.trackId == callback.trackId) {
                    songName = inlineBtn.text
                    break
                }
            }
        }
        songName = songName.substringBeforeLast(" (")

        val storageResult = yandexMusic.findStorage(callback.trackId, callback.artistId)
        if (storageResult is ResultOf.failure) { return storageResult }
        val storage = (storageResult as ResultOf.success).value

        val fileLocationResult = yandexMusic.findFileLocation(storage, callback.searchString)
        if (fileLocationResult is ResultOf.failure) { return fileLocationResult }
        val fileLocation = (fileLocationResult as ResultOf.success).value

        val streamResult = yandexMusic.downloadFileAsStream(fileLocation, songName, callback.searchString)
        if (streamResult is ResultOf.failure) { return streamResult }
        val stream = (streamResult as ResultOf.success).value

        val sendDocument = SendDocument()
        sendDocument.chatId = update.callbackQuery.message.chatId.toString()
        sendDocument.document = InputFile(stream, String.format("%s.mp3", songName))
        return ResultOf.success(sendDocument)
    }

    /**
     * Print callback buttons with tracks to download
     * */
    private fun createTrackButtons(trackList: List<TrackItem>, rowList: MutableList<List<InlineKeyboardButton>>, searchText: String, artistName: String? = null) {
        trackList.map { track ->
            val inlineKeyboardButton1 = InlineKeyboardButton()
            val millis = track.durationMs.toLong()
            val duration = String.format("(%d:%02d)", TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))

            val trackName = artistName
                ?.let { "${track.title} - $artistName" }
                ?: track.artists.getOrNull(0)?.name
                ?.let { "${track.title} - $it" } ?: track.title

            inlineKeyboardButton1.text = "$trackName $duration"
            inlineKeyboardButton1.callbackData = TrackCallback(track.id, track.albums[0].id, searchText).encode()
            val keyboardButtonsRow1: MutableList<InlineKeyboardButton> = ArrayList()
            keyboardButtonsRow1.add(inlineKeyboardButton1)
            rowList.add(keyboardButtonsRow1)
        }
    }

    /**
     * Print button with similar artists callback
     * */
    private fun artistButtonShowMoreRow(artistId: Int): MutableList<InlineKeyboardButton> {
        val artistsButtonsRow: MutableList<InlineKeyboardButton> = ArrayList()
        val inlineArtistButton = InlineKeyboardButton()
        inlineArtistButton.text = "Show more similar artists..."
        inlineArtistButton.callbackData = SimilarCallback(artistId).encode()
        artistsButtonsRow.add(inlineArtistButton)
        return artistsButtonsRow
    }

    /**
     * Show similar artists button on a row
     * */
    private fun artistButtonsRow(items: List<ArtistItem>, searchText: String): MutableList<InlineKeyboardButton> {
        val artistsButtonsRow: MutableList<InlineKeyboardButton> = ArrayList()
        items.map {
            val inlineArtistButton = InlineKeyboardButton()
            inlineArtistButton.text = it.name
            inlineArtistButton.callbackData = ArtistTrackWithPagesCallback(1, it.id, searchText).encode()
            artistsButtonsRow.add(inlineArtistButton)
        }
        return artistsButtonsRow
    }

    /**
     * Create buttons with page numbers for tracks
     * */
    private fun pagesButtonsRow(total: Int, current: Int, searchText: String, artistId: Int? = null): MutableList<InlineKeyboardButton> {
        val pagesButtonsRow: MutableList<InlineKeyboardButton> = ArrayList()
        val totalPages = total / TRACKS_PER_PAGE
        val (left, right) = calculatePagesLimit(current, totalPages)

        // create page buttons
        for (i in left..right) {
            val inlinePageButton = InlineKeyboardButton()
            inlinePageButton.text = if (i == current) "*$i*" else i.toString()
            if (artistId != null) {
                inlinePageButton.callbackData = ArtistTrackWithPagesCallback(i, artistId, searchText).encode()
            } else {
                inlinePageButton.callbackData = SearchTrackWithPagesCallback(i, searchText).encode()
            }
            pagesButtonsRow.add(inlinePageButton)
        }
        return pagesButtonsRow
    }

    /**
     * Calculate pages limits with shifting for [current] position and [totalPages]
     * */
    private fun calculatePagesLimit(current: Int, totalPages: Int, pagesLimit: Int = API_BUTTONS_ROW_LIMIT): Pair<Int, Int> {
        // left pages
        var left = 1
        // Api limited for 8 buttons in a row
        var right = if (totalPages > pagesLimit) pagesLimit else totalPages

        // calc pages limits
        if (current >= right / 2) {
            left = current - (pagesLimit / 2) + 1
            right = left + pagesLimit - 1
            if (right > totalPages) {
                left += totalPages - right
                right = totalPages
            }
            if (left < 1) {
                left = 1
            }
        }
        return Pair(left, right)
    }

}