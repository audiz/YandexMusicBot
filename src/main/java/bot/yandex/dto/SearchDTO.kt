package bot.yandex.dto

import bot.yandex.dto.domain.Artists
import bot.yandex.dto.domain.Tracks

data class SearchDTO (val tracks: Tracks, val artists: Artists)
