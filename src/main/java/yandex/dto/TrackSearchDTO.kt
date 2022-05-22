package yandex.dto

import yandex.dto.search.PagerDTO
import yandex.dto.search.TracksDTO

data class TrackSearchDTO(
    val tracks: TracksDTO,
    val pager: PagerDTO
)