package yandex

import yandex.dto.ArtistSearchDTO
import yandex.dto.download.DownloadInfo
import yandex.dto.SearchDTO
import yandex.dto.TrackSearchDTO
import yandex.dto.download.Storage
import java.io.InputStream

interface YandexMusic {
    fun search(search: String): SearchDTO
    fun searchTrack(search: String, page: Int = 0): TrackSearchDTO
    fun searchTrack(artistId: Int): ArtistSearchDTO
    fun getArtist(id: Int): ArtistSearchDTO
    fun getSimilar(artistId: Int): ArtistSearchDTO
    fun findStorage(trackId: Int, artistId: Int): Storage
    fun findFileLocation(storageDTO: Storage, search: String): DownloadInfo
    fun downloadFile(downloadInfo: DownloadInfo, songName: String, search: String)
    fun downloadFileAsStream(downloadInfo: DownloadInfo, songName: String, search: String): InputStream
}