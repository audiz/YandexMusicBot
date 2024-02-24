package bot.yandex.dto

import bot.yandex.dto.domain.Pager
import bot.yandex.dto.domain.Tracks

data class TrackSearchDTO(val tracks: Tracks, val pager: Pager)