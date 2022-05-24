package yandex.dto

import yandex.dto.domain.Artists
import yandex.dto.domain.Tracks

data class SearchDTO (
    val tracks: Tracks,
    val artists: Artists
)
