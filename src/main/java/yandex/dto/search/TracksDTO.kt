package yandex.dto.search

data class TracksDTO(
    val items: List<TrackItemDTO>,
    val total: Int
)