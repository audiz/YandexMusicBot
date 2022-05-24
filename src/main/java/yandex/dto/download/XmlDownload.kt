package yandex.dto.download

import com.fasterxml.jackson.annotation.JsonProperty

data class XmlDownload(
    @JsonProperty("download-info")
    val downloadInfo: DownloadInfo
)