package yandex

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.GetMethod
import org.json.JSONObject
import org.json.XML
import org.springframework.stereotype.Component
import yandex.dto.ArtistSearchDTO
import yandex.dto.download.DownloadInfoDTO
import yandex.dto.SearchDTO
import yandex.dto.TrackSearchDTO
import yandex.dto.download.StorageDTO
import yandex.dto.download.XmlDownloadDTO
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Component("yandexParserInterface")
class YandexMusicImpl: YandexMusic {

    companion object {
        val client = HttpClient()
        val mapper = jacksonObjectMapper()
        init {
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
    }

    @Throws(Exception::class)
    override fun search(search: String): SearchDTO {

        val getMethod = GetMethod("https://music.yandex.ru/handlers/music-search.jsx?text=$search&type=all&ncrnd=0.04640457655433827&clientNow=${System.currentTimeMillis()}&lang=ru&external-domain=")
        getMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:99.0) Gecko/20100101 Firefox/99.0")
        getMethod.setRequestHeader("Accept", "application/json, text/javascript, */*; q=0.01")
        getMethod.setRequestHeader("Accept-Language", "en-US,en;q=0.5")
        getMethod.setRequestHeader("X-Requested-With", "XMLHttpRequest")
        getMethod.setRequestHeader("Connection", "keep-alive")
        getMethod.setRequestHeader("Sec-Fetch-Dest", "empty")
        getMethod.setRequestHeader("Sec-Fetch-Mode", "cors")
        getMethod.setRequestHeader("Sec-Fetch-Site", "same-origin")

        client.executeMethod(getMethod)
        val jsonString = getMethod.responseBodyAsString
        //println(jsonString)

        val searchData = mapper.readValue(jsonString, SearchDTO::class.java)
        //println(searchData)

        return searchData
    }

    override fun searchTrack(search: String, page: Int): TrackSearchDTO {
       val getMethod = GetMethod("https://music.yandex.ru/handlers/music-search.jsx?text=$search&type=tracks&page=$page&ncrnd=0.04640457655433827&clientNow=${System.currentTimeMillis()}&lang=ru&external-domain=")
        getMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:99.0) Gecko/20100101 Firefox/99.0")
        getMethod.setRequestHeader("Accept", "application/json, text/javascript, */*; q=0.01")
        getMethod.setRequestHeader("Accept-Language", "en-US,en;q=0.5")
        getMethod.setRequestHeader("X-Requested-With", "XMLHttpRequest")
        getMethod.setRequestHeader("Connection", "keep-alive")
        getMethod.setRequestHeader("Sec-Fetch-Dest", "empty")
        getMethod.setRequestHeader("Sec-Fetch-Mode", "cors")
        getMethod.setRequestHeader("Sec-Fetch-Site", "same-origin")

        client.executeMethod(getMethod)
        val jsonString = getMethod.responseBodyAsString
        //println(jsonString)

        val searchData = mapper.readValue(jsonString, TrackSearchDTO::class.java)
        //println(searchData)

        return searchData
    }

    override fun searchTrack(artistId: Int): ArtistSearchDTO {
        val getMethod = GetMethod("https://music.yandex.ru/handlers/artist.jsx?artist=$artistId&what=tracks&sort=&dir=&period=&lang=ru&external-domain=")
        getMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:99.0) Gecko/20100101 Firefox/99.0")
        getMethod.setRequestHeader("Accept", "application/json, text/javascript, */*; q=0.01")
        getMethod.setRequestHeader("Accept-Language", "en-US,en;q=0.5")
        getMethod.setRequestHeader("X-Requested-With", "XMLHttpRequest")
        getMethod.setRequestHeader("Connection", "keep-alive")
        getMethod.setRequestHeader("Sec-Fetch-Dest", "empty")
        getMethod.setRequestHeader("Sec-Fetch-Mode", "cors")
        getMethod.setRequestHeader("Sec-Fetch-Site", "same-origin")

        client.executeMethod(getMethod)
        val jsonString = getMethod.responseBodyAsString
        //println(jsonString)

        val searchData = mapper.readValue(jsonString, ArtistSearchDTO::class.java)
        //println(searchData)

        return searchData
    }



    override fun getArtist(id: Int): ArtistSearchDTO {
        val getMethod = GetMethod("https://music.yandex.ru/handlers/artist.jsx?artist=$id&lang=ru&external-domain=")
        getMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:99.0) Gecko/20100101 Firefox/99.0")
        getMethod.setRequestHeader("Accept", "application/json, text/javascript, */*; q=0.01")
        getMethod.setRequestHeader("Accept-Language", "en-US,en;q=0.5")
        getMethod.setRequestHeader("X-Requested-With", "XMLHttpRequest")
        getMethod.setRequestHeader("Connection", "keep-alive")
        getMethod.setRequestHeader("Sec-Fetch-Dest", "empty")
        getMethod.setRequestHeader("Sec-Fetch-Mode", "cors")
        getMethod.setRequestHeader("Sec-Fetch-Site", "same-origin")

        client.executeMethod(getMethod)
        val jsonString = getMethod.responseBodyAsString
        //println(jsonString)

        val artistData = mapper.readValue(jsonString, ArtistSearchDTO::class.java)
        //println(artistData)

        return artistData
    }

    override fun findStorage(trackId: Int, artistId: Int, search: String): StorageDTO {
        val getMethod =
            GetMethod("https://music.yandex.ru/api/v2.1/handlers/track/$trackId:$artistId/web-search-track-track-saved/download/m?hq=0&external-domain=")
        getMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:99.0) Gecko/20100101 Firefox/99.0")
        getMethod.setRequestHeader("Accept", "application/json; q=1.0, text/*; q=0.8, */*; q=0.1")
        getMethod.setRequestHeader("Accept-Language", "en-US,en;q=0.5")
        getMethod.setRequestHeader("Referer", "https://music.yandex.ru/search?text=$search")
        getMethod.setRequestHeader("X-Retpath-Y", "https%3A%2F%2Fmusic.yandex.ru%2Fsearch%3Ftext%3D$search")
        getMethod.setRequestHeader("X-Yandex-Music-Client", "YandexMusicAPI")
        getMethod.setRequestHeader("X-Current-UID", "23858391")
        getMethod.setRequestHeader("X-Requested-With", "XMLHttpRequest")
        getMethod.setRequestHeader("Connection", "keep-alive")
        getMethod.setRequestHeader(
            "Cookie",
            "Session_id=3:1652866985.5.0.1631726116372:qerc1A:15.1.2:1|23858391.0.2|3:252517.474832.isxX6h4Gn2xIy3fzgVKMHWhx7ys"
        )
        getMethod.setRequestHeader("Sec-Fetch-Dest", "empty")
        getMethod.setRequestHeader("Sec-Fetch-Mode", "cors")
        getMethod.setRequestHeader("Sec-Fetch-Site", "same-origin")

        client.executeMethod(getMethod)
        val jsonString = getMethod.responseBodyAsString

        return mapper.readValue(jsonString, StorageDTO::class.java)
    }

    override fun findFileLocation(storageDTO: StorageDTO, search: String): DownloadInfoDTO {
        val getMethod = GetMethod("https:${storageDTO.src}")
        getMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:99.0) Gecko/20100101 Firefox/99.0")
        getMethod.setRequestHeader("Accept", "application/json; q=1.0, text/*; q=0.8, */*; q=0.1")
        getMethod.setRequestHeader("Accept-Language", "en-US,en;q=0.5")
        getMethod.setRequestHeader("Referer", "https://music.yandex.ru/search?text=$search")
        getMethod.setRequestHeader("Origin", "https://music.yandex.ru")
        getMethod.setRequestHeader("Connection", "keep-alive")
        getMethod.setRequestHeader("Sec-Fetch-Dest", "empty")
        getMethod.setRequestHeader("Sec-Fetch-Mode", "cors")
        getMethod.setRequestHeader("Sec-Fetch-Site", "cross-site")

        client.executeMethod(getMethod)
        val xmlString = getMethod.responseBodyAsString

        val xmlJSONObj: JSONObject = XML.toJSONObject(xmlString)
        val jsonPrettyPrintString = xmlJSONObj.toString(0)

        val xmlDownloadDTO = mapper.readValue(jsonPrettyPrintString, XmlDownloadDTO::class.java)
        return xmlDownloadDTO.downloadInfo
    }

    override fun downloadFile(downloadInfo: DownloadInfoDTO, songName: String, search: String) {
        val getMethod = GetMethod("https://${downloadInfo.host}/get-mp3/68cb293afda65aea883b5abc5dea8dbb/${downloadInfo.ts}${downloadInfo.path}")
        getMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:99.0) Gecko/20100101 Firefox/99.0")
        getMethod.setRequestHeader("Accept", "application/json; q=1.0, text/*; q=0.8, */*; q=0.1")
        getMethod.setRequestHeader("Accept-Language", "en-US,en;q=0.5")
        getMethod.setRequestHeader("Referer", "https://music.yandex.ru/search?text=$search")
        getMethod.setRequestHeader("Origin", "https://music.yandex.ru")
        getMethod.setRequestHeader("Connection", "keep-alive")
        getMethod.setRequestHeader("Sec-Fetch-Dest", "empty")
        getMethod.setRequestHeader("Sec-Fetch-Mode", "cors")
        getMethod.setRequestHeader("Sec-Fetch-Site", "cross-site")

        client.executeMethod(getMethod)

        val inputStream = getMethod.responseBodyAsStream

        Files.copy(inputStream, Paths.get("/home/max/Downloads/$songName.mp3"), StandardCopyOption.REPLACE_EXISTING);
    }

    override fun downloadFileAsStream(downloadInfo: DownloadInfoDTO, songName: String, search: String): InputStream {
        val getMethod = GetMethod("https://${downloadInfo.host}/get-mp3/68cb293afda65aea883b5abc5dea8dbb/${downloadInfo.ts}${downloadInfo.path}")
        getMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:99.0) Gecko/20100101 Firefox/99.0")
        getMethod.setRequestHeader("Accept", "application/json; q=1.0, text/*; q=0.8, */*; q=0.1")
        getMethod.setRequestHeader("Accept-Language", "en-US,en;q=0.5")
        getMethod.setRequestHeader("Referer", "https://music.yandex.ru/search?text=$search")
        getMethod.setRequestHeader("Origin", "https://music.yandex.ru")
        getMethod.setRequestHeader("Connection", "keep-alive")
        getMethod.setRequestHeader("Sec-Fetch-Dest", "empty")
        getMethod.setRequestHeader("Sec-Fetch-Mode", "cors")
        getMethod.setRequestHeader("Sec-Fetch-Site", "cross-site")

        client.executeMethod(getMethod)

        return getMethod.responseBodyAsStream
    }
}