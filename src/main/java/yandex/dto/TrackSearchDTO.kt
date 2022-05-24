package yandex.dto

import yandex.dto.domain.Pager
import yandex.dto.domain.Tracks

data class TrackSearchDTO(
    val tracks: Tracks,
    val pager: Pager
)