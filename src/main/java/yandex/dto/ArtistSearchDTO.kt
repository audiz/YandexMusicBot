package yandex.dto

import yandex.dto.domain.Artist
import yandex.dto.domain.ArtistItem
import yandex.dto.domain.TrackItem

data class ArtistSearchDTO(
    val artist: Artist,
    val tracks: List<TrackItem>,
    val similar: List<ArtistItem>,
    val allSimilar: List<ArtistItem>,
    val trackIds: List<String>
)