package bot.telegram.callback

import bot.common.ErrorBuilder
import bot.common.ErrorKind
import bot.common.ResultOf
import bot.telegram.repository.UserStorage
import bot.telegram.service.MessageSender
import bot.yandex.YandexMusic
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import java.io.InputStream
import java.util.*
import java.util.concurrent.Executors

@Component
class MusicDownloadActionsImpl(val yandexMusic: YandexMusic,
                               val messageSource: MessageSource,
                               val userStorage: UserStorage,
                               val messageSender: MessageSender
                               ): MusicDownloadActions {

    /**
     * Cancel download list
     * */
    override fun cancelDownloadMp3List(userId: Long, chatId: String): ResultOf<SendMessage> {
        executor.shutdownNow()
        executor = Executors.newSingleThreadExecutor()
        userStorage.clearStorageData(userId)

        val answer = SendMessage()
        answer.text = messageSource.getMessage("download.latest.playlist.canceled", null, Locale.getDefault())
        answer.chatId = chatId
        return ResultOf.Success(answer)
    }

    /**
     * Download list of tracks
     * */
    override fun downloadMp3List(userId: Long, chatId: String, callback: DownloadPlaylistCallback, absSender: DefaultAbsSender): ResultOf<SendMessage> {
        val storage = userStorage.getUserStorageData(userId)
            ?: return ResultOf.Failure(
                ErrorBuilder.newBuilder(ErrorKind.APP_INTERNAL)
                .withCode(404)
                .withDescription(messageSource.getMessage("errors.latest.playlists.not.found", null, Locale.getDefault())))

        if (storage.loaded[callback.position] == true) {
            return ResultOf.Failure(
                ErrorBuilder.newBuilder(ErrorKind.APP_INTERNAL)
                .withCode(403)
                .withDescription(messageSource.getMessage("errors.latest.playlists.position.used", null, Locale.getDefault())))
        }
        val inProgress: Boolean = !((storage.loaded.find{ it == false }) ?: true)
        if (inProgress) {
            return ResultOf.Failure(
                ErrorBuilder.newBuilder(ErrorKind.APP_INTERNAL)
                .withCode(403)
                .withDescription(messageSource.getMessage("errors.latest.playlists.position.using", null, Locale.getDefault())))
        }

        // now we can download
        val subList = storage.tracks.subList(callback.trackFrom, callback.trackTo)
        //"Storage download form ${callback.trackFrom} to ${callback.trackTo}"

        executor.submit {
            userStorage.startDownloadTracks(userId, callback.position)
            var used = false
            for (track in subList) {
                Thread.sleep(2000)
                val artists = track.artists.joinToString(separator = ", ") { it.name }
                val songName = "${track.title} - $artists"
                messageSender.sendSimpleMsg(chatId, absSender, songName)

                /*if (!used) {
                    used = true
                    val pair = getTrackStream(track.id, track.albums[0].id, songName, storage.searchText)
                    if (pair.first != null) {
                        return@Callable pair.first!!
                    }
                    val stream = pair.second
                    val sendDocument = SendDocument()
                    sendDocument.chatId = chatId
                    sendDocument.document = InputFile(stream, String.format("%s.mp3", songName))
                    messageSender.sendMessage(userId, chatId, ResultOf.Success(sendDocument), absSender)
                }*/
            }
            userStorage.stopDownloadTracks(userId, callback.position)
        }

        //playlistActions.showDownloadPlaylist(userId, chatId)

        /*if (result is ResultOf.Failure) {
            return result
        }*/
        val sendMessage = SendMessage()
        sendMessage.chatId = chatId
        sendMessage.text = messageSource.getMessage("download.latest.playlist.started", null, Locale.getDefault())

        return ResultOf.Success(sendMessage)
    }

    /**
     * Download track and send it to user
     * */
    override fun downloadMp3(userId: Long, chatId: String, callback: TrackCallback, keyboardList: List<List<InlineKeyboardButton>>): ResultOf<SendDocument> {
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

        val pair = getTrackStream(callback.trackId, callback.artistId, songName, callback.searchString)
        if (pair.first != null) {
            return pair.first!!
        }
        val stream = pair.second

        val sendDocument = SendDocument()
        sendDocument.chatId = chatId
        sendDocument.document = InputFile(stream, String.format("%s.mp3", songName))
        return ResultOf.Success(sendDocument)
    }

    private fun getTrackStream(trackId: Int, artistId: Int, songName: String, searchString: String): Pair<ResultOf.Failure?, InputStream?> {
        val storageResult = yandexMusic.findStorage(trackId, artistId)
        if (storageResult is ResultOf.Failure) { return Pair(storageResult, null) }
        val storage = (storageResult as ResultOf.Success).value

        val fileLocationResult = yandexMusic.findFileLocation(storage, searchString)
        if (fileLocationResult is ResultOf.Failure) { return Pair(fileLocationResult, null) }
        val fileLocation = (fileLocationResult as ResultOf.Success).value

        val streamResult = yandexMusic.downloadFileAsStream(fileLocation, songName, searchString)
        if (streamResult is ResultOf.Failure) { return Pair(streamResult, null) }
        val stream = (streamResult as ResultOf.Success).value

        return Pair(null, stream)
    }

    companion object {
        var executor = Executors.newSingleThreadExecutor()
    }
}