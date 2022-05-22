package yandex

import yandex.dto.ArtistSearchDTO
import yandex.dto.download.DownloadInfoDTO
import yandex.dto.SearchDTO
import yandex.dto.TrackSearchDTO
import yandex.dto.download.StorageDTO
import java.io.InputStream

interface YandexMusic {
    fun search(search: String): SearchDTO

    fun searchTrack(search: String, page: Int = 0): TrackSearchDTO

    fun searchTrack(artistId: Int): ArtistSearchDTO

    fun getArtist(id: Int): ArtistSearchDTO

    fun findStorage(trackId: Int, artistId: Int, search: String): StorageDTO

    fun findFileLocation(storageDTO: StorageDTO, search: String): DownloadInfoDTO

    fun downloadFile(downloadInfo: DownloadInfoDTO, songName: String, search: String)

    fun downloadFileAsStream(downloadInfo: DownloadInfoDTO, songName: String, search: String): InputStream
}