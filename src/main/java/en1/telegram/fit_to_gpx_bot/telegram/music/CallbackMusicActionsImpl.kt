package en1.telegram.fit_to_gpx_bot.telegram.music

import en1.telegram.fit_to_gpx_bot.telegram.callback.CallbackTypes
import en1.telegram.fit_to_gpx_bot.telegram.callback.dto.*
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

private const val ARTISTS_PER_PAGE = 10
private const val TRACKS_PER_PAGE = 10
private const val API_BUTTONS_ROW_LIMIT = 8

/**
 * Handler for callback user actions
 * */
@Component
class CallbackMusicActionsImpl(val yandexMusic: YandexMusic, val callbackTypes: CallbackTypes) : CallbackMusicActions {
    companion object {
        private val logger = LoggerFactory.getLogger(CallbackMusicActionsImpl::class.java)
    }

    /**
     * Pagination for artists tracks with similar artists.
     * Limit for json, when more than 150 we should request by ids
     * */
    override fun artistWithPagesMsg(update: Update, callback: ArtistTrackWithPagesCallback): SendMessage {
        val page = callback.page

        val artistSearch = yandexMusic.searchTrack(callback.artistId)
        val rowList: MutableList<List<InlineKeyboardButton>> = ArrayList()

        createTracksButtons(artistSearch.tracks.drop((page - 1) * TRACKS_PER_PAGE).take(TRACKS_PER_PAGE), rowList, callback.searchString, artistSearch.artist.name)
        rowList.add(pagesButtonsRow(artistSearch.tracks.size, page, callback.searchString, callback.artistId))
        rowList.add(artistButtonsRow(artistSearch.similar.take(3), callback.searchString))

        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        inlineKeyboardMarkup.keyboard = rowList

        val answer = SendMessage()
        answer.text = "Artist page: $page (${(page * TRACKS_PER_PAGE)} of ${artistSearch.tracks.size} tracks)"
        answer.chatId = update.callbackQuery.message.chatId.toString()
        answer.replyMarkup = inlineKeyboardMarkup

        return answer
    }

    /**
     * Pagination for searched tracks
     * */
    override fun searchTrackWithPagesMsg(update: Update, callback: SearchTrackWithPagesCallback): SendMessage {
        //logger.info("processPagerTrackSearch callback = {}", callback)
        val page = callback.page
        val realPageForRequest = ((page - 1) * TRACKS_PER_PAGE) / 100
        val search = yandexMusic.searchTrack(callback.searchString, realPageForRequest)
        val rowList: MutableList<List<InlineKeyboardButton>> = ArrayList()

        val total = if (search.tracks.total > 200) 200 else search.tracks.total

        createTracksButtons(search.tracks.items.drop(((page - 1) % TRACKS_PER_PAGE) * TRACKS_PER_PAGE).take(TRACKS_PER_PAGE), rowList, callback.searchString)
        rowList.add(pagesButtonsRow(total, page, callback.searchString))

        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        inlineKeyboardMarkup.keyboard = rowList

        val answer = SendMessage()
        answer.text = "Search page: $page (${(page * TRACKS_PER_PAGE)} of ${search.tracks.total} tracks)"
        answer.chatId = update.callbackQuery.message.chatId.toString()
        answer.replyMarkup = inlineKeyboardMarkup

        return answer
    }

    override fun similarMsg(update: Update, callback: SimilarCallback): SendMessage {
        val artistSearch = yandexMusic.getSimilar(callback.artistId)

        val rowList: MutableList<List<InlineKeyboardButton>> = ArrayList()

        artistSearch.allSimilar.take(3 * ARTISTS_PER_PAGE).chunked(3).map {
            rowList.add(artistButtonsRow(it, ""))
        }

        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        inlineKeyboardMarkup.keyboard = rowList

        val answer = SendMessage()
        answer.text = "Similar artists for: ${artistSearch.artist.name}"
        answer.chatId = update.callbackQuery.message.chatId.toString()
        answer.replyMarkup = inlineKeyboardMarkup
        return answer
    }

    /**
     * Show search tracks to user
     * */
    override fun introMsg(msg: Message): SendMessage {

        // TODO cut string to limit
        val searchText = URLEncoder.encode(msg.text.trimIndent(), "utf-8")
        val search = yandexMusic.search(searchText)

        val rowList: MutableList<List<InlineKeyboardButton>> = ArrayList()

        createTracksButtons(search.tracks.items, rowList, searchText)
        rowList.add(pagesButtonsRow(search.tracks.total, -1, searchText))
        rowList.add(artistButtonsRow(search.artists.items, searchText))

        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        inlineKeyboardMarkup.keyboard = rowList

        val answer = SendMessage()
        answer.text = "Tracks found for request: ${search.tracks.total}"
        answer.chatId = msg.chatId.toString()
        answer.replyMarkup = inlineKeyboardMarkup

        return answer
    }

    /**
     * Show artists tracks to user
     * */
    override fun artistMsg(update: Update, callback: ArtistCallback): SendMessage {
        val artistSearch = yandexMusic.getArtist(callback.artistId)

        val rowList: MutableList<List<InlineKeyboardButton>> = ArrayList()
        createTracksButtons(artistSearch.tracks, rowList, callback.searchString, artistSearch.artist.name)
        rowList.add(pagesButtonsRow(artistSearch.trackIds.size, -1,  callback.searchString, callback.artistId))
        rowList.add(artistButtonsRow(artistSearch.similar.take(3), callback.searchString))
        rowList.add(artistButtonsShowMoreRow(callback.artistId))

        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        inlineKeyboardMarkup.keyboard = rowList

        val answer = SendMessage()
        answer.text = "Artist found: ${callback.name}"
        answer.chatId = update.callbackQuery.message.chatId.toString()
        answer.replyMarkup = inlineKeyboardMarkup

        return answer
    }

    /**
     * Download track and send it to user
     * */
    override fun document(update: Update, callback: TrackCallback): SendDocument {
        var songName = ""
        for (keyboard in update.callbackQuery.message.replyMarkup.keyboard) {
            for (inlineBtn in keyboard) {
                if (callbackTypes.parseCallback(inlineBtn.callbackData, TrackCallback::class.java)?.trackId == callback.trackId) {
                    songName = inlineBtn.text
                    break
                }
            }
        }

        val storage = yandexMusic.findStorage(callback.trackId, callback.artistId, callback.searchString)
        val fileLocation = yandexMusic.findFileLocation(storage, callback.searchString)
        val stream = yandexMusic.downloadFileAsStream(fileLocation, songName, callback.searchString)
        val sendDocument = SendDocument()
        sendDocument.chatId = update.callbackQuery.message.chatId.toString()
        sendDocument.document = InputFile(stream, String.format("%s.mp3", songName))
        return sendDocument
    }

    private fun createTracksButtons(items: List<TrackItem>, rowList: MutableList<List<InlineKeyboardButton>>, searchText: String, artistName: String? = null) {
        items.map {
            val inlineKeyboardButton1 = InlineKeyboardButton()

            var artistName = artistName
            if (artistName == null && it.artists.isNotEmpty()) {
                artistName = it.artists[0].name
            }

            val songFullName = if (artistName != null)  "${it.title} - $artistName" else it.title
            inlineKeyboardButton1.text = songFullName
            inlineKeyboardButton1.callbackData = callbackTypes.trackCallback(it.id, it.albums[0].id, searchText)
            val keyboardButtonsRow1: MutableList<InlineKeyboardButton> = ArrayList()
            keyboardButtonsRow1.add(inlineKeyboardButton1)
            rowList.add(keyboardButtonsRow1)
        }
    }

    private fun artistButtonsShowMoreRow(artistId: Int): MutableList<InlineKeyboardButton> {
        val artistsButtonsRow: MutableList<InlineKeyboardButton> = ArrayList()
        val inlineArtistButton = InlineKeyboardButton()
        inlineArtistButton.text = "Show more similar artists..."
        inlineArtistButton.callbackData = callbackTypes.similarCallback(artistId)
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
            inlineArtistButton.callbackData = callbackTypes.artistCallback(it.id, it.name, searchText)
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

        // left pages
        var left = 1
        // Api limited for 8 buttons in a row
        var right = if (totalPages > API_BUTTONS_ROW_LIMIT) API_BUTTONS_ROW_LIMIT else totalPages

        // calc pages limits
        if (current >= right / 2) {
            left = current - (API_BUTTONS_ROW_LIMIT / 2) + 1
            right = left + API_BUTTONS_ROW_LIMIT - 1
            if (right > totalPages) {
                left += totalPages - right
                right = totalPages
            }
        }

        // create pages buttons
        for (i in left..right) {
            val inlinePageButton = InlineKeyboardButton()
            inlinePageButton.text = if (i == current) "*$i*" else i.toString()
            if (artistId != null) {
                inlinePageButton.callbackData = callbackTypes.artistTracksWithPagesCallback(i, artistId, searchText)
            } else {
                inlinePageButton.callbackData = callbackTypes.searchTracksWithPagesCallback(i, searchText)
            }
            pagesButtonsRow.add(inlinePageButton)
        }
        return pagesButtonsRow
    }


}