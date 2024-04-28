package bot.telegram.callback

import bot.common.ErrorBuilder
import bot.common.ErrorKind
import bot.common.ResultOf
import bot.common.returnNok
import bot.telegram.repository.UserStorage
import bot.yandex.YandexMusic
import bot.yandex.dto.domain.ArtistItem
import bot.yandex.dto.domain.TrackItem
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import java.net.URLEncoder
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

private const val ARTISTS_PER_PAGE = 10
private const val TRACKS_PER_PAGE = 10
private const val API_BUTTONS_ROW_LIMIT = 8

/**
 * Handler for callback user actions
 * */
@Component
class CallbackMusicActionsImpl(val yandexMusic: YandexMusic, val messageSource: MessageSource, val userStorage: UserStorage) : CallbackMusicActions {
    private val logger = LoggerFactory.getLogger(CallbackMusicActionsImpl::class.java)

    override fun showDownloadPlaylist(userId: Long, chatId: String): ResultOf<SendMessage> {
        val tracks = userStorage.getLatestTracks(userId)
        if (tracks.isNullOrEmpty()) {
            return ResultOf.Failure(ErrorBuilder.newBuilder(ErrorKind.APP_INTERNAL)
                .withCode(404)
                .withDescription(messageSource.getMessage("errors.latest.playlists.not.found", null, Locale.getDefault())))
        }
        val rowList: MutableList<List<InlineKeyboardButton>> = ArrayList()

        val inlineKeyboardButton1 = InlineKeyboardButton()
        inlineKeyboardButton1.text = "Load first 10 tracks"
        inlineKeyboardButton1.callbackData = DownloadPlaylistCallback(userId, 0, 10).encode()
        val keyboardButtonsRow1: MutableList<InlineKeyboardButton> = ArrayList()
        keyboardButtonsRow1.add(inlineKeyboardButton1)
        rowList.add(keyboardButtonsRow1)

        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        inlineKeyboardMarkup.keyboard = rowList

        val answer = SendMessage()
        answer.text = messageSource.getMessage("download.latest.playlist", null, Locale.getDefault())
        answer.chatId = chatId
        answer.replyMarkup = inlineKeyboardMarkup

        return ResultOf.Success(answer)
    }

    /**
     * Show personal playlists for yandex user
     * */
    override fun getPersonalPlaylists(chatId: String): ResultOf<SendMessage> {
        val dailyIdResult = yandexMusic.getPlaylists()
        dailyIdResult.returnNok { return it }
        val dailyPlaylists = (dailyIdResult as ResultOf.Success).value
        val playlists = dailyPlaylists.blocks
            .filter { it.type == "personal-playlists" }
            .flatMap { blocks -> blocks.entities.map { Triple(it.data.data?.title, it.data.data?.kind, it.data.data?.owner?.login) }
        }

        val rowList: MutableList<List<InlineKeyboardButton>> = ArrayList()

        playlists.forEach {
            val inlineKeyboardButton1 = InlineKeyboardButton()
            inlineKeyboardButton1.text = it.first!!
            inlineKeyboardButton1.callbackData = PlaylistCallback(it.second!!, it.third!!).encode()
            val keyboardButtonsRow1: MutableList<InlineKeyboardButton> = ArrayList()
            keyboardButtonsRow1.add(inlineKeyboardButton1)
            rowList.add(keyboardButtonsRow1)
        }

        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        inlineKeyboardMarkup.keyboard = rowList

        val answer = SendMessage()
        //answer.text = "Personal Playlists"
        answer.text = messageSource.getMessage("playlist.personal.list", null, Locale.getDefault())
        answer.chatId = chatId
        answer.replyMarkup = inlineKeyboardMarkup

        return ResultOf.Success(answer)
    }

    override fun playlist(userId: Long, chatId: String, callback: PlaylistCallback): ResultOf<SendMessage> {
        val playlistResult = yandexMusic.getPlaylist(callback.kind, callback.owner)
        playlistResult.returnNok { return it }
        val playlist = (playlistResult as ResultOf.Success).value

        val rowList: MutableList<List<InlineKeyboardButton>> = ArrayList()

        createTrackButtons(userId, playlist.playlist.tracks.take(60), rowList, "")

        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        inlineKeyboardMarkup.keyboard = rowList
        val answer = SendMessage()
        answer.text = "Playlist tracks count for user is ${playlist.playlist.tracks.size}"
        answer.chatId = chatId
        answer.replyMarkup = inlineKeyboardMarkup

        return ResultOf.Success(answer)
    }

    /**
     * Daily playlist for current yandex user
     * */
    override fun dailyPlaylist(userId: Long, chatId: String): ResultOf<SendMessage> {
        val dailyIdResult = yandexMusic.getPlaylists()
        dailyIdResult.returnNok { return it }
        val dailyPlaylists = (dailyIdResult as ResultOf.Success).value
        val (playlistId, owner) = dailyPlaylists.blocks.map { blocks -> blocks.entities.find { it.data.data?.title == "Плейлист дня" } }
            .first()?.let { return@let Pair(it.data.data?.kind, it.data.data?.owner?.login) } ?: Pair(null, null)
        //val playlistId = 153850731

        val dailyPlaylist = yandexMusic.getPlaylist(playlistId!!, owner!!)
        dailyPlaylist.returnNok { return it }
        val dailyList = (dailyPlaylist as ResultOf.Success).value

        val rowList: MutableList<List<InlineKeyboardButton>> = ArrayList()
        createTrackButtons(userId, dailyList.playlist.tracks.take(60), rowList, "")

        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        inlineKeyboardMarkup.keyboard = rowList
        val answer = SendMessage()

        answer.text = messageSource.getMessage("playlist.daily.result", arrayOf(dailyList.playlist.tracks.size), Locale.getDefault())
        answer.chatId = chatId
        answer.replyMarkup = inlineKeyboardMarkup

        return ResultOf.Success(answer)
    }

    /**
     * Show search tracks to user
     * */
    override fun searchByString(userId: Long, chatId: String, text: String): ResultOf<SendMessage> {
        // TODO cut string to limit
        val searchText = URLEncoder.encode(text.trimIndent(), "utf-8")
        val searchResult = yandexMusic.search(searchText)
        searchResult.returnNok { return it }

        val search = (searchResult as ResultOf.Success).value
        val rowList: MutableList<List<InlineKeyboardButton>> = ArrayList()

        createTrackButtons(userId, search.tracks.items, rowList, searchText)
        rowList.add(pagesButtonsRow(search.tracks.total, -1, searchText))
        rowList.add(artistButtonsRow(search.artists.items, searchText))

        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        inlineKeyboardMarkup.keyboard = rowList

        val answer = SendMessage()
        answer.text = "Tracks found for request: ${search.tracks.total}"
        answer.chatId = chatId
        answer.replyMarkup = inlineKeyboardMarkup

        return ResultOf.Success(answer)
    }

    /**
     * Pagination for artists tracks with similar artists.
     * Limit for json, when more than 150 we should request by ids
     * */
    override fun artistWithPagesMsg(userId: Long, chatId: String, callback: ArtistTrackWithPagesCallback): ResultOf<SendMessage> {
        val page = callback.page
        val searchResult = yandexMusic.searchTrack(callback.artistId)
        searchResult.returnNok { return it }

        val artistSearch = (searchResult as ResultOf.Success).value
        val rowList: MutableList<List<InlineKeyboardButton>> = ArrayList()

        createTrackButtons(userId, artistSearch.tracks.drop((page - 1) * TRACKS_PER_PAGE).take(TRACKS_PER_PAGE), rowList, callback.searchString)
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

        return ResultOf.Success(answer)
    }

    /**
     * Pagination for searched tracks
     * */
    override fun searchWithPagesMsg(userId: Long, chatId: String, callback: SearchTrackWithPagesCallback): ResultOf<SendMessage> {
        //logger.info("searchWithPagesMsg callback = {}", callback)
        val page = callback.page
        val realPageForRequest = ((page - 1) * TRACKS_PER_PAGE) / 100
        val searchResult = yandexMusic.searchTrack(callback.searchString, realPageForRequest)
        searchResult.returnNok { return it }

        val search = (searchResult as ResultOf.Success).value
        val rowList: MutableList<List<InlineKeyboardButton>> = ArrayList()
        val total = if (search.tracks.total > 200) 200 else search.tracks.total

        createTrackButtons(
            userId,
            search.tracks.items.drop(((page - 1) % TRACKS_PER_PAGE) * TRACKS_PER_PAGE).take(TRACKS_PER_PAGE),
            rowList,
            callback.searchString
        )
        rowList.add(pagesButtonsRow(total, page, callback.searchString))

        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        inlineKeyboardMarkup.keyboard = rowList

        val answer = SendMessage()
        answer.text = "Search page: $page (${(page * TRACKS_PER_PAGE)} of ${search.tracks.total} tracks)"
        answer.chatId = chatId
        answer.replyMarkup = inlineKeyboardMarkup

        return ResultOf.Success(answer)
    }

    /**
     * Show similar artists
     * */
    override fun similarMsg(userId: Long, chatId: String, callback: SimilarCallback): ResultOf<SendMessage> {
        val searchResult = yandexMusic.getSimilar(callback.artistId)
        searchResult.returnNok { return it }

        val artistSearch = (searchResult as ResultOf.Success).value
        val rowList: MutableList<List<InlineKeyboardButton>> = ArrayList()
        artistSearch.allSimilar.take(3 * ARTISTS_PER_PAGE).chunked(3).map { rowList.add(artistButtonsRow(it, "")) }

        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        inlineKeyboardMarkup.keyboard = rowList

        val answer = SendMessage()
        answer.text = "Artists similar to ${artistSearch.artist.name}"
        answer.chatId = chatId
        answer.replyMarkup = inlineKeyboardMarkup
        return ResultOf.Success(answer)
    }

    /**
     * Download track and send it to user
     * */
    override fun document(userId: Long, chatId: String, callback: TrackCallback, keyboardList: List<List<InlineKeyboardButton>>): ResultOf<SendDocument> {
        var songName = ""
        for (keyboard in keyboardList) {
            for (inlineBtn in keyboard) {
                if (inlineBtn.callbackData.decodeCallback(TrackCallback::class.java)?.trackId == callback.trackId) {
                    songName = inlineBtn.text
                    break
                }
            }
        }
        songName = songName.substringBeforeLast(" (")

        val storageResult = yandexMusic.findStorage(callback.trackId, callback.artistId)
        if (storageResult is ResultOf.Failure) { return storageResult }
        val storage = (storageResult as ResultOf.Success).value

        val fileLocationResult = yandexMusic.findFileLocation(storage, callback.searchString)
        if (fileLocationResult is ResultOf.Failure) { return fileLocationResult }
        val fileLocation = (fileLocationResult as ResultOf.Success).value

        val streamResult = yandexMusic.downloadFileAsStream(fileLocation, songName, callback.searchString)
        if (streamResult is ResultOf.Failure) { return streamResult }
        val stream = (streamResult as ResultOf.Success).value

        val sendDocument = SendDocument()
        sendDocument.chatId = chatId
        sendDocument.document = InputFile(stream, String.format("%s.mp3", songName))
        return ResultOf.Success(sendDocument)
    }

    /**
     * Send [answer] for [captcha] to yandex and return result
     * */
    override fun answerCaptcha(chatId: String, answer: String, captcha: ResultOf.Captcha): ResultOf<SendMessage> {
        val captchaResult = yandexMusic.answerCaptcha(answer, captcha)
        val sendMessage = SendMessage()
        sendMessage.text = "Captcha result is $captchaResult"
        sendMessage.chatId = chatId
        return ResultOf.Success(sendMessage)
    }

    /**
     * Print callback buttons with tracks to download
     * */
    private fun createTrackButtons(userId: Long, trackList: List<TrackItem>, rowList: MutableList<List<InlineKeyboardButton>>, searchText: String) {
        userStorage.saveLatestTracks(userId, trackList)

        trackList.map { track ->
            val inlineKeyboardButton1 = InlineKeyboardButton()
            val millis = track.durationMs.toLong()
            val duration = String.format("(%d:%02d)", TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))

            val artists = track.artists.joinToString(separator = ", ") { it.name }
            val trackName = "${track.title} - $artists"

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