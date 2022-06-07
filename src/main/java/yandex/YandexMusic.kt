package yandex

import en1.common.ResultOf
import yandex.dto.ArtistSearchDTO
import yandex.dto.download.DownloadInfo
import yandex.dto.SearchDTO
import yandex.dto.TrackSearchDTO
import yandex.dto.download.Storage
import java.io.InputStream

interface YandexMusic {
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