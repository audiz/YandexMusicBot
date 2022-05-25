package yandex

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.json.JSONObject
import org.json.XML
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import yandex.dto.ArtistSearchDTO
import yandex.dto.SearchDTO
import yandex.dto.TrackSearchDTO
import yandex.dto.download.DownloadInfo
import yandex.dto.download.Storage
import yandex.dto.download.XmlDownload
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Component("yandexParserInterface")
class YandexMusicImpl: YandexMusic {

    companion object {
        private val logger = LoggerFactory.getLogger(YandexMusicImpl::class.java)
        val httpClient: CloseableHttpClient = HttpClients.createDefault()
        val mapper = jacksonObjectMapper()
        init {
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
    }

    private val yandexCookie: String?

    init {
        val getenv = System.getenv()
        yandexCookie = getenv!!["YANDEX_COOKIE"]!!
    }

    @Throws(Exception::class)
    override fun search(search: String): SearchDTO {

        val request = HttpGet("https://music.yandex.ru/handlers/music-search.jsx?text=$search&type=all&ncrnd=0.04640457655433827&clientNow=${System.currentTimeMillis()}&lang=ru&external-domain=")
        request.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:99.0) Gecko/20100101 Firefox/99.0")
        request.addHeader("Accept", "application/json, text/javascript, */*; q=0.01")
        request.addHeader("Accept-Language", "en-US,en;q=0.5")
        request.addHeader("X-Requested-With", "XMLHttpRequest")
        request.addHeader("Connection", "keep-alive")
        request.addHeader("Sec-Fetch-Dest", "empty")
        request.addHeader("Sec-Fetch-Mode", "cors")
        request.addHeader("Sec-Fetch-Site", "same-origin")

        val response = httpClient.execute(request)
        val entity = response.entity
        val jsonString = EntityUtils.toString(entity)
        //println(jsonString)

        val searchData = mapper.readValue(jsonString, SearchDTO::class.java)
        //println(searchData)

        return searchData
    }

    override fun searchTrack(search: String, page: Int): TrackSearchDTO {
        val request = HttpGet("https://music.yandex.ru/handlers/music-search.jsx?text=$search&type=tracks&page=$page&ncrnd=0.04640457655433827&clientNow=${System.currentTimeMillis()}&lang=ru&external-domain=")
        request.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:99.0) Gecko/20100101 Firefox/99.0")
        request.addHeader("Accept", "application/json, text/javascript, */*; q=0.01")
        request.addHeader("Accept-Language", "en-US,en;q=0.5")
        request.addHeader("X-Requested-With", "XMLHttpRequest")
        request.addHeader("Connection", "keep-alive")
        request.addHeader("Sec-Fetch-Dest", "empty")
        request.addHeader("Sec-Fetch-Mode", "cors")
        request.addHeader("Sec-Fetch-Site", "same-origin")

        val response = httpClient.execute(request)
        val entity = response.entity
        val jsonString = EntityUtils.toString(entity)
        //println(jsonString)

        val searchData = mapper.readValue(jsonString, TrackSearchDTO::class.java)
        //println(searchData)

        return searchData
    }

    override fun searchTrack(artistId: Int): ArtistSearchDTO {
        val request = HttpGet("https://music.yandex.ru/handlers/artist.jsx?artist=$artistId&what=tracks&sort=&dir=&period=&lang=ru&external-domain=")
        request.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:99.0) Gecko/20100101 Firefox/99.0")
        request.addHeader("Accept", "application/json, text/javascript, */*; q=0.01")
        request.addHeader("Accept-Language", "en-US,en;q=0.5")
        request.addHeader("X-Requested-With", "XMLHttpRequest")
        request.addHeader("Connection", "keep-alive")
        request.addHeader("Sec-Fetch-Dest", "empty")
        request.addHeader("Sec-Fetch-Mode", "cors")
        request.addHeader("Sec-Fetch-Site", "same-origin")

        val response = httpClient.execute(request)
        val entity = response.entity
        val jsonString = EntityUtils.toString(entity)
        //println(jsonString)

        val searchData = mapper.readValue(jsonString, ArtistSearchDTO::class.java)
        //println(searchData)

        return searchData
    }



    override fun getArtist(id: Int): ArtistSearchDTO {
        val request = HttpGet("https://music.yandex.ru/handlers/artist.jsx?artist=$id&lang=ru&external-domain=")
        request.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:99.0) Gecko/20100101 Firefox/99.0")
        request.addHeader("Accept", "application/json, text/javascript, */*; q=0.01")
        request.addHeader("Accept-Language", "en-US,en;q=0.5")
        request.addHeader("X-Requested-With", "XMLHttpRequest")
        request.addHeader("Connection", "keep-alive")
        request.addHeader("Sec-Fetch-Dest", "empty")
        request.addHeader("Sec-Fetch-Mode", "cors")
        request.addHeader("Sec-Fetch-Site", "same-origin")

        val response = httpClient.execute(request)
        val entity = response.entity
        val jsonString = EntityUtils.toString(entity)
        //println(jsonString)

        val artistData = mapper.readValue(jsonString, ArtistSearchDTO::class.java)
        //println(artistData)

        return artistData
    }

    override fun getSimilar(artistId: Int): ArtistSearchDTO {
        val request = HttpGet("https://music.yandex.ru/handlers/artist.jsx?artist=$artistId&what=similar&sort=&dir=&period=&lang=ru&external-domain=")
        request.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:99.0) Gecko/20100101 Firefox/99.0")
        request.addHeader("Accept", "application/json, text/javascript, */*; q=0.01")
        request.addHeader("Accept-Language", "en-US,en;q=0.5")
        request.addHeader("Referer", "https://music.yandex.ru/artist/$artistId/similar")
        request.addHeader("X-Current-UID", "23858391")
        request.addHeader("X-Retpath-Y", "https://music.yandex.ru/artist/$artistId/similar")
        request.addHeader("X-Requested-With", "XMLHttpRequest")
        request.addHeader("Connection", "keep-alive")
        request.addHeader("Sec-Fetch-Dest", "empty")
        request.addHeader("Sec-Fetch-Mode", "cors")
        request.addHeader("Sec-Fetch-Site", "same-origin")

        val response = httpClient.execute(request)
        val entity = response.entity
        val jsonString = EntityUtils.toString(entity)
        //println(jsonString)

        val artistData = mapper.readValue(jsonString, ArtistSearchDTO::class.java)
        //println(artistData)

        return artistData
    }

    // TODO return nullable and wrap answer to object
    override fun findStorage(trackId: Int, artistId: Int): Storage {
        val request = HttpGet("https://music.yandex.ru/api/v2.1/handlers/track/$trackId:$artistId/web-search-track-track-saved/download/m?hq=0&external-domain=")
        request.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:99.0) Gecko/20100101 Firefox/99.0")
        request.addHeader("Accept", "application/json; q=1.0, text/*; q=0.8, */*; q=0.1")
        request.addHeader("Accept-Language", "en-US,en;q=0.5")
        request.addHeader("Referer", "https://music.yandex.ru/artist/$artistId/tracks")
        request.addHeader("X-Retpath-Y", "https%3A%2F%2Fmusic.yandex.ru%2Fartist%2F$artistId%2Ftracks")
        request.addHeader("X-Yandex-Music-Client", "YandexMusicAPI")
        request.addHeader("X-Current-UID", "23858391")
        request.addHeader("X-Requested-With", "XMLHttpRequest")
        request.addHeader("Connection", "keep-alive")
        if (yandexCookie != null) {
            request.addHeader("Cookie", yandexCookie)
        }
        request.addHeader("Sec-Fetch-Dest", "empty")
        request.addHeader("Sec-Fetch-Mode", "cors")
        request.addHeader("Sec-Fetch-Site", "same-origin")

        val response = httpClient.execute(request)
        val entity = response.entity
        val jsonString = EntityUtils.toString(entity)

        if (jsonString.isEmpty()) {
            logger.warn("Failed to get storage for trackId = $trackId, artistId = $artistId")
        }

        return mapper.readValue(jsonString, Storage::class.java)
    }

    override fun findFileLocation(storageDTO: Storage, search: String): DownloadInfo {
        val request = HttpGet("https:${storageDTO.src}")
        request.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:99.0) Gecko/20100101 Firefox/99.0")
        request.addHeader("Accept", "application/json; q=1.0, text/*; q=0.8, */*; q=0.1")
        request.addHeader("Accept-Language", "en-US,en;q=0.5")
        if (search.isNotEmpty()) {
            request.addHeader("Referer", "https://music.yandex.ru/search?text=$search")
        }
        request.addHeader("Origin", "https://music.yandex.ru")
        request.addHeader("Connection", "keep-alive")
        request.addHeader("Sec-Fetch-Dest", "empty")
        request.addHeader("Sec-Fetch-Mode", "cors")
        request.addHeader("Sec-Fetch-Site", "cross-site")

        val response = httpClient.execute(request)
        val entity = response.entity
        val xmlString = EntityUtils.toString(entity)

        val xmlJSONObj: JSONObject = XML.toJSONObject(xmlString)
        val jsonPrettyPrintString = xmlJSONObj.toString(0)

        val xmlDownloadDTO = mapper.readValue(jsonPrettyPrintString, XmlDownload::class.java)
        return xmlDownloadDTO.downloadInfo
    }

    override fun downloadFile(downloadInfo: DownloadInfo, songName: String, search: String) {
        val request = HttpGet("https://${downloadInfo.host}/get-mp3/68cb293afda65aea883b5abc5dea8dbb/${downloadInfo.ts}${downloadInfo.path}")
        request.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:99.0) Gecko/20100101 Firefox/99.0")
        request.addHeader("Accept", "application/json; q=1.0, text/*; q=0.8, */*; q=0.1")
        request.addHeader("Accept-Language", "en-US,en;q=0.5")
        if (search.isNotEmpty()) {
            request.addHeader("Referer", "https://music.yandex.ru/search?text=$search")
        }
        request.addHeader("Origin", "https://music.yandex.ru")
        request.addHeader("Connection", "keep-alive")
        request.addHeader("Sec-Fetch-Dest", "empty")
        request.addHeader("Sec-Fetch-Mode", "cors")
        request.addHeader("Sec-Fetch-Site", "cross-site")

        val response = httpClient.execute(request)
        val inputStream = response.entity.content

        Files.copy(inputStream, Paths.get("/home/max/Downloads/$songName.mp3"), StandardCopyOption.REPLACE_EXISTING);
    }

    override fun downloadFileAsStream(downloadInfo: DownloadInfo, songName: String, search: String): InputStream {
        val request = HttpGet("https://${downloadInfo.host}/get-mp3/68cb293afda65aea883b5abc5dea8dbb/${downloadInfo.ts}${downloadInfo.path}")
        request.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:99.0) Gecko/20100101 Firefox/99.0")
        request.addHeader("Accept", "application/json; q=1.0, text/*; q=0.8, */*; q=0.1")
        request.addHeader("Accept-Language", "en-US,en;q=0.5")
        if (search.isNotEmpty()) {
            request.addHeader("Referer", "https://music.yandex.ru/search?text=$search")
        }
        request.addHeader("Origin", "https://music.yandex.ru")
        request.addHeader("Connection", "keep-alive")
        request.addHeader("Sec-Fetch-Dest", "empty")
        request.addHeader("Sec-Fetch-Mode", "cors")
        request.addHeader("Sec-Fetch-Site", "cross-site")

        return httpClient.execute(request).entity.content
    }
}