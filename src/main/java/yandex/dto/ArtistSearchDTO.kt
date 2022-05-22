package yandex.dto

import yandex.dto.search.ArtistDTO
import yandex.dto.search.ArtistItemDTO
import yandex.dto.search.TrackItemDTO

data class ArtistSearchDTO(
    val artist: ArtistDTO,
    val tracks: List<TrackItemDTO>,
    val similar: List<ArtistItemDTO>,
    val trackIds: List<String>
)