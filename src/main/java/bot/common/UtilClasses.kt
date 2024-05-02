package bot.common

import bot.telegram.callback.Callback
import bot.telegram.repository.model.UserStorageData
import bot.yandex.dto.domain.CaptchaInfo
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Response States for requests
 * */
sealed class ResultOf<out T> {
    data class Success<out R>(val value: R, val header: Map<String, String>? = null): ResultOf<R>()
    data class Failure(val errorBuilder: ErrorBuilder): ResultOf<Nothing>()
    data class Captcha(val captcha: CaptchaInfo, var callback: Callback? = null): ResultOf<Nothing>()
    //class None: ResultOf<Nothing>()
}


/**
 * Tun [block] to return non success result instantly
 * */
inline fun <reified T> ResultOf<T>.returnNok(block: (ResultOf<Nothing>) -> Unit) {
    if (this is ResultOf.Failure) {
        return block(this as ResultOf<Nothing>)
    }
    if (this is ResultOf.Captcha) {
        return block(this as ResultOf<Nothing>)
    }
}


fun String.urlEncode(): String = URLEncoder.encode(this, java.nio.charset.StandardCharsets.UTF_8.toString())
fun String.urlDecode(): String = URLDecoder.decode(this, java.nio.charset.StandardCharsets.UTF_8.toString())

inline fun UserStorageData?.isDownloadProcess(block: () -> Unit) {
    if (this == null) {
        return
    }
    val inProgress: Boolean = !((this.loaded.find{ it == false }) ?: true)
    if (inProgress) {
        return block()
    }
}