package yandex.dto.playlist

data class Data(
    val kind: Long,
    val title: String,
    val description: String,
    val trackCount: String,
    val owner: Owner
    )
