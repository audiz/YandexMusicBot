package bot.telegram.repository

import bot.telegram.repository.model.UserStorageData
import bot.yandex.dto.domain.TrackItem
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.ceil

@Component
class UserStorageImpl: UserStorage {
    private val map: ConcurrentHashMap<Long, UserStorageData> = ConcurrentHashMap()
    private val DEFAULT_DOWNLOAD_SIZE = 10.0

    override fun saveLatestTracks(id: Long, tracks: List<TrackItem>) {
        map.compute(id) { _, v ->
            val size = ceil(tracks.size / DEFAULT_DOWNLOAD_SIZE).toInt()
            val loaded = arrayOfNulls<Boolean>(size)
            return@compute v?.also<UserStorageData> {
                it.tracks = tracks
                it.loaded = loaded
            } ?: UserStorageData(tracks, loaded)
        }
    }

    override fun getLatestTracks(id: Long): List<TrackItem>? {
        return map[id]?.tracks
    }
}