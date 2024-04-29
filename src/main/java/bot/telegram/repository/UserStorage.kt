package bot.telegram.repository

import bot.telegram.repository.model.UserStorageData
import bot.yandex.dto.domain.TrackItem

interface UserStorage {

    fun saveLatestTracks(userId: Long, tracks: List<TrackItem>, searchText: String)

    fun getUserStorageData(userId: Long): UserStorageData?

    fun startDownloadTracks(userId: Long, position: Int)

    fun stopDownloadTracks(userId: Long, position: Int)

    fun clearStorageData(userId: Long)

}