package bot.yandex.dto

import bot.yandex.dto.domain.Artist
import bot.yandex.dto.domain.ArtistItem
import bot.yandex.dto.domain.TrackItem

data class ArtistSearchDTO(
    val artist: Artist,
    val tracks: List<TrackItem>,
    val similar: List<ArtistItem>,
    val allSimilar: List<ArtistItem>,
    val trackIds: List<String>
)