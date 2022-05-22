package yandex.dto

import yandex.dto.search.ArtistsDTO
import yandex.dto.search.TracksDTO

data class SearchDTO (
    val tracks: TracksDTO,
    val artists: ArtistsDTO
)
