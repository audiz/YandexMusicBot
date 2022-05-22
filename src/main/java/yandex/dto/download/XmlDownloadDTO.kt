package yandex.dto.download

import com.fasterxml.jackson.annotation.JsonProperty

data class XmlDownloadDTO(
    @JsonProperty("download-info")
    val downloadInfo: DownloadInfoDTO
)