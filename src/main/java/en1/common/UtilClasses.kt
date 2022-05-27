package en1.common

import en1.telegram.bot.telegram.callback.Callback
import yandex.dto.domain.CaptchaInfo

/**
 * Response States for requests
 * */
sealed class ResultOf<out T> {
    data class success<out R>(val value: R, val header: Map<String, String>? = null): ResultOf<R>()
    data class failure(val message: String, val code: Int): ResultOf<Nothing>()
    data class captcha(val capthca: CaptchaInfo, var callback: Callback?): ResultOf<Nothing>()
}
