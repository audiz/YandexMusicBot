package yandex

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import en1.common.*
import org.apache.http.client.config.CookieSpecs
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.json.JSONObject
import org.json.XML
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import yandex.dto.*
import yandex.dto.download.DownloadInfo
import yandex.dto.download.Storage
import yandex.dto.download.XmlDownload
import java.io.InputStream

@Component
class YandexMusicImpl(val envConfiguration: EnvConfiguration): YandexMusic {
    companion object {
        private val logger = LoggerFactory.getLogger(YandexMusicImpl::class.java)
        private val requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()
        val httpClient: CloseableHttpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build()
        val mapper = jacksonObjectMapper()
        init {
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
        private val DEFAULT_HEADERS = mapOf(
            "User-Agent" to "Mozilla/5.0 (X11; Linux x86_64; rv:99.0) Gecko/20100101 Firefox/99.0",
            "Accept" to "application/json, text/javascript, */*; q=0.01",
            "Accept-Language" to "en-US,en;q=0.5",
            "X-Requested-With" to "XMLHttpRequest",
            "Connection" to "keep-alive",
            "Sec-Fetch-Dest" to "empty",
            "Sec-Fetch-Mode" to "cors",
            "Sec-Fetch-Site" to "same-origin"
        )
    }

    override fun getPlaylists(): ResultOf<PlaylistsDTO> {
        try {
            val headers = mutableMapOf("Referer" to "https://music.yandex.ru/home", "X-Retpath-Y" to "https://music.yandex.ru/home")
            envConfiguration.getYandexCookie()?.let { headers["Cookie"] = it }

            val url = "https://music.yandex.ru/handlers/main.jsx?what=home&lang=ru&external-domain="
            val jsonString = httpGet(url, headers) { return it }
            //logger.info("jsonString = {}", jsonString)
            val playlistsDTO = mapper.readValue(jsonString, PlaylistsDTO::class.java)
            return ResultOf.Success(playlistsDTO)
        } catch (e: Throwable) {
            e.printStackTrace()
            return ResultOf.Failure(e.message!!, ERROR_YANDEX_REQUEST_FAILED)
        }
    }

    override fun getPlaylist(playlistId: Long, owner: String): ResultOf<DailyDTO> {
        try {
            val url = "https://music.yandex.ru/handlers/playlist.jsx?owner=$owner&kinds=$playlistId&light=true&madeFor=&lang=ru&external-domain="
            val headers = mutableMapOf(
                "X-Current-UID" to "23858391",
                "Referer" to "https://music.yandex.ru/home",
                "X-Retpath-Y" to "https://music.yandex.ru/home"
            )
            envConfiguration.getYandexCookie()?.let { headers["Cookie"] = it }

            val jsonString = httpGet(url, headers) { return it }
            val searchData = mapper.readValue(jsonString, DailyDTO::class.java)
            //println(searchData)
            return ResultOf.Success(searchData)
        } catch (e: Throwable) {
            e.printStackTrace()
            return ResultOf.Failure(e.message!!, ERROR_YANDEX_REQUEST_FAILED)
        }
    }

    override fun search(search: String): ResultOf<SearchDTO> {
        try {
            val url = "https://music.yandex.ru/handlers/music-search.jsx?text=$search&type=all&clientNow=${System.currentTimeMillis()}&lang=ru&external-domain="
            val jsonString = httpGet(url) { return it }
            val searchData = mapper.readValue(jsonString, SearchDTO::class.java)
            return ResultOf.Success(searchData)
        } catch (e: Throwable) {
            e.printStackTrace()
            return ResultOf.Failure(e.message!!, ERROR_YANDEX_REQUEST_FAILED)
        }
    }

    override fun searchTrack(search: String, page: Int): ResultOf<TrackSearchDTO> {
        try {
            val url = "https://music.yandex.ru/handlers/music-search.jsx?text=$search&type=tracks&page=$page&clientNow=${System.currentTimeMillis()}&lang=ru&external-domain="
            val jsonString = httpGet(url) { return it }
            val searchData = mapper.readValue(jsonString, TrackSearchDTO::class.java)
            //println(searchData)
            return ResultOf.Success(searchData)
        } catch (e: Throwable) {
            e.printStackTrace()
            return ResultOf.Failure(e.message!!, ERROR_YANDEX_REQUEST_FAILED)
        }
    }

    override fun searchTrack(artistId: Int): ResultOf<ArtistSearchDTO> {
        try {
            val url = "https://music.yandex.ru/handlers/artist.jsx?artist=$artistId&what=tracks&sort=&dir=&period=&lang=ru&external-domain="
            val jsonString = httpGet(url) { return it }
            val searchData = mapper.readValue(jsonString, ArtistSearchDTO::class.java)
            //println(searchData)
            return ResultOf.Success(searchData)
        } catch (e: Throwable) {
            e.printStackTrace()
            return ResultOf.Failure(e.message!!, ERROR_YANDEX_REQUEST_FAILED)
        }
    }

    override fun getArtist(id: Int): ResultOf<ArtistSearchDTO> {
        try {
            val url = "https://music.yandex.ru/handlers/artist.jsx?artist=$id&lang=ru&external-domain="
            val jsonString = httpGet(url) { return it }
            val artistData = mapper.readValue(jsonString, ArtistSearchDTO::class.java)
            //println(artistData)
            return ResultOf.Success(artistData)
        } catch (e: Throwable) {
            e.printStackTrace()
            return ResultOf.Failure(e.message!!, ERROR_YANDEX_REQUEST_FAILED)
        }
    }

    override fun answerCaptcha(answer: String, captcha: ResultOf.Captcha): Boolean {
        val captchaKey = captcha.captcha.key.urlEncode()
        val retpath = captcha.captcha.captchaPage.substringAfter("retpath=").substringBefore("&")
        val u = captcha.captcha.captchaPage.substringAfter("u=").substringBefore("&")
        val request = HttpGet("https://music.yandex.ru/checkcaptcha?key=${captchaKey}&retpath=${retpath}&u=${u}&rep=${answer.urlEncode()}")
        val response = httpClient.execute(request)
        val answerString = EntityUtils.toString(response.entity)
        logger.info("statusCode = {}, answerString = {}", response.statusLine.statusCode, answerString)
        return true
    }

    override fun getSimilar(artistId: Int): ResultOf<ArtistSearchDTO> {
        try {
            val headers = mapOf(
                "Referer" to "https://music.yandex.ru/artist/$artistId/similar",
                "X-Retpath-Y" to "https://music.yandex.ru/artist/$artistId/similar"
            )
            val url = "https://music.yandex.ru/handlers/artist.jsx?artist=$artistId&what=similar"
            val jsonString = httpGet(url, headers) { return it }
            val artistData = mapper.readValue(jsonString, ArtistSearchDTO::class.java)
            //println(artistData)
            return ResultOf.Success(artistData)
        } catch (e: Throwable) {
            e.printStackTrace()
            return ResultOf.Failure(e.message!!, ERROR_YANDEX_REQUEST_FAILED)
        }
    }

    // TODO return nullable and wrap answer to object
    override fun findStorage(trackId: Int, artistId: Int): ResultOf<Storage> {
        try {
            val headers = mutableMapOf(
                "Referer" to "https://music.yandex.ru/artist/$artistId/tracks",
                "X-Retpath-Y" to "https%3A%2F%2Fmusic.yandex.ru%2Fartist%2F$artistId%2Ftracks",
                "X-Yandex-Music-Client" to "YandexMusicAPI"
            )
            envConfiguration.getYandexCookie()?.let { headers["Cookie"] = it }

            val url = "https://music.yandex.ru/api/v2.1/handlers/track/$trackId:$artistId/web-search-track-track-saved/download/m?hq=0&external-domain="
            val jsonString = httpGet(url, headers) { return it }
            val storageData = mapper.readValue(jsonString, Storage::class.java)
            return ResultOf.Success(storageData)
        } catch (e: Throwable) {
            e.printStackTrace()
            return ResultOf.Failure(e.message!!, ERROR_YANDEX_REQUEST_FAILED)
        }
    }

    override fun findFileLocation(storageDTO: Storage, search: String): ResultOf<DownloadInfo> {
        try {
            val headers = mutableMapOf(
                "Origin" to "https://music.yandex.ru",
                "X-Requested-With" to null,
                "Sec-Fetch-Site" to "cross-site"
            )
            if (search.isNotEmpty()) {
                headers["Referer"] = "https://music.yandex.ru/search?text=$search"
            }
            val url = "https:${storageDTO.src}"
            val xmlString = httpGet(url, headers) { return it }
            val xmlJSONObj: JSONObject = XML.toJSONObject(xmlString)
            val jsonPrettyPrintString = xmlJSONObj.toString(0)

            val xmlDownloadDTO = mapper.readValue(jsonPrettyPrintString, XmlDownload::class.java)
            return ResultOf.Success(xmlDownloadDTO.downloadInfo)
        } catch (e: Throwable) {
            e.printStackTrace()
            return ResultOf.Failure(e.message!!, ERROR_YANDEX_REQUEST_FAILED)
        }
    }

    override fun downloadFileAsStream(downloadInfo: DownloadInfo, songName: String, search: String): ResultOf<InputStream> {
        var failureMsg = ""
        try {
            failureMsg = "Request to /get-mp3/"
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

            return ResultOf.Success(httpClient.execute(request).entity.content)
        } catch (e: Throwable) {
            return ResultOf.Failure(failureMsg, ERROR_YANDEX_REQUEST_FAILED)
        }
    }

    private inline fun httpGet(url: String, map: Map<String, String?>? = null, block: (ResultOf<Nothing>) -> String): String {
        var failureMsg  = "Request to ${url.substringAfter("https://music.yandex.ru/handlers")}"
        val request = HttpGet(url)

        val combinedHeaders: Map<String, String?> = DEFAULT_HEADERS + (map ?: mapOf())
        combinedHeaders.forEach { (key, value) -> if (value != null) request.addHeader(key, value) }

        val response = httpClient.execute(request)
        val entity = response.entity
        val jsonString = EntityUtils.toString(entity)
        failureMsg += ", jsonString = $jsonString, statusCode = ${response.statusLine.statusCode}"

        if (jsonString.contains("\"type\": \"captcha\"")) {
            val captcha = mapper.readValue(jsonString, CaptchaDTO::class.java)
            logger.info("captcha = {}", captcha)
            return block(ResultOf.Captcha(captcha.captcha))
        }
        return jsonString
    }
}

/**
 * Captcha info https://zennolab.com/discussion/threads/ne-peredaet-kapchu-cherez-get-v-jandeks-500r.71479/
1. https://yandex.ru/search/?text=Nokian%20Tyres%20Hakkapeliitta%20R3%20SUV&lr=213

2. https://yandex.ru/showcaptcha?retpath=https%3A//yandex.ru/search%3Ftext%3DNokian%2520Tyres%2520Hakkapeliitta%2520R3%2520SUV%26lr%3D213_588b7ac2ffec570ccaf34175752f8292&t=0/1577600475/4aeb08da4d0bf27fbf0c95652d753e12&s=37ecff107c0311563d5edc81f917a296

3. https://yandex.ru/captchaimg?aHR0cHM6Ly9leHQuY2FwdGNoYS55YW5kZXgubmV0L2ltYWdlP2tleT0wMEFDUkZXQldzeGVNdUl0WDFOOTdDRElvMTZIRGF6aiZzZXJ2aWNlPXdlYg,,_0/1577600475/4aeb08da4d0bf27fbf0c95652d753e12_d6fd48a98df72f2cb5125edeedac0532

4. https://yandex.ru/checkcaptcha?key=00ACRFWBWsxeMuItX1N97CDIo16HDazj_0%2F1577600475%2F4aeb08da4d0bf27fbf0c95652d753e12_fbf57a16d5d7e4d5ef12bff5e732911c&retpath=https%3A%2F%2Fyandex.ru%2Fsearch%3Ftext%3DNokian%2520Tyres%2520Hakkapeliitta%2520R3%2520SUV%26lr%3D213_588b7ac2ffec570ccaf34175752f8292&rep=bacteria+%D0%A4%D0%9E%D0%A0%D0%9C%D0%90%D0%A2%D0%95
 * */