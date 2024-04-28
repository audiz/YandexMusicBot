package bot.telegram.repository

import bot.common.DEFAULT_DOWNLOAD_SIZE
import bot.telegram.repository.model.UserStorageData
import bot.yandex.dto.domain.TrackItem
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.ceil

@Component
class UserStorageImpl: UserStorage {
    private val map: ConcurrentHashMap<Long, UserStorageData> = ConcurrentHashMap()

    override fun saveLatestTracks(userId: Long, tracks: List<TrackItem>, searchText: String) {
        map.compute(userId) { _, v ->
            val size = ceil(tracks.size / DEFAULT_DOWNLOAD_SIZE).toInt()
            val loaded = arrayOfNulls<Boolean>(size)
            return@compute v?.also<UserStorageData> {
                it.tracks = tracks
                it.loaded = loaded
                it.searchText = searchText
            } ?: UserStorageData(tracks, loaded, searchText)
        }
    }

    override fun getUserStorageData(userId: Long): UserStorageData? {
        return map[userId]
    }

    override fun startDownloadTracks(userId: Long, position: Int) {
        map.computeIfPresent(userId) { _, v ->
            v.loaded[position] = false
            v
        }
    }

    override fun stopDownloadTracks(userId: Long, position: Int) {
        map.computeIfPresent(userId) { _, v ->
            v.loaded[position] = true
            v
        }
    }
}