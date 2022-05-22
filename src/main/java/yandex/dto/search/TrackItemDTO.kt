package yandex.dto.search

data class TrackItemDTO (
    val id: Int,
    val realId: Int?,
    val title: String,
    val available: Boolean,
    val durationMs: Int,
    val albums: List<AlbumDTO>,
    val artists: List<ArtistDTO>
    )
