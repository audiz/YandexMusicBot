package bot.yandex.dto.domain

data class TrackItem (
    val id: Int,
    val realId: Int?,
    val title: String,
    val available: Boolean,
    val durationMs: Int,
    val albums: List<Album>,
    val artists: List<Artist>)
