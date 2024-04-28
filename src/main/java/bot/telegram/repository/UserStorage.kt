package bot.telegram.repository

import bot.yandex.dto.domain.TrackItem

interface UserStorage {
    fun saveLatestTracks(id: Long, tracks: List<TrackItem>)
    fun getLatestTracks(id: Long): List<TrackItem>?
}