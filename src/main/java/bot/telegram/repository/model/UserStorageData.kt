package bot.telegram.repository.model

import bot.yandex.dto.domain.TrackItem

data class UserStorageData(var tracks: List<TrackItem>, var loaded: Array<Boolean?>)
