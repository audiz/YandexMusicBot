package bot.yandex

import bot.yandex.dto.*
import bot.common.ResultOf
import bot.yandex.dto.download.DownloadInfo
import bot.yandex.dto.download.Storage
import java.io.InputStream

interface YandexMusic {

    /**
     * Return playlists generated for yandex user
     * TODO: different types: personal-playlists, podcasts, promotions, new-releases, new-playlists, chart, play-contexts, mixes.
     * */
    fun getPlaylists(): ResultOf<PlaylistsDTO>
    /**
     * Return playlist by [playlistId]
     * */
    fun getPlaylist(playlistId: Long, owner: String): ResultOf<DailyDTO>
    fun search(search: String): ResultOf<SearchDTO>
    fun searchTrack(search: String, page: Int = 0): ResultOf<TrackSearchDTO>
    fun searchTrack(artistId: Int): ResultOf<ArtistSearchDTO>
    fun getArtist(id: Int): ResultOf<ArtistSearchDTO>
    fun getSimilar(artistId: Int): ResultOf<ArtistSearchDTO>
    fun findStorage(trackId: Int, artistId: Int): ResultOf<Storage>
    fun findFileLocation(storageDTO: Storage, search: String): ResultOf<DownloadInfo>
    fun downloadFileAsStream(downloadInfo: DownloadInfo, songName: String, search: String): ResultOf<InputStream>
    fun answerCaptcha(answer: String, captcha: ResultOf.Captcha): Boolean
}