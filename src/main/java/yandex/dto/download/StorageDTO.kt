package yandex.dto.download

data class StorageDTO (
    val codec: String,
    val bitrate: Int,
    val src: String,
    val gain: Boolean,
    val preview: Boolean
    )