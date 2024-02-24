package bot.yandex.dto.download

data class Storage (
    val codec: String,
    val bitrate: Int,
    val src: String,
    val gain: Boolean,
    val preview: Boolean)