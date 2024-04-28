package bot.telegram.repository.model

import bot.yandex.dto.domain.TrackItem

/**
 * [searchText] is required as referrer to make request to yandex
 * */
data class UserStorageData(var tracks: List<TrackItem>, var loaded: Array<Boolean?>, var searchText: String)
