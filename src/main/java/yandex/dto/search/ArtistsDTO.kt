package yandex.dto.search

data class ArtistsDTO(
    val items: List<ArtistItemDTO>,
    val total: Int
)