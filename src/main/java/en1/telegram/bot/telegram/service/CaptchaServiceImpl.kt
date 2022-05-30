package en1.telegram.bot.telegram.service

import en1.common.ResultOf
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class CaptchaServiceImpl : CaptchaService {
    private val captchaMap = ConcurrentHashMap<Int, ResultOf.Captcha>()

    override fun put(userId: Int, captcha: ResultOf.Captcha) {
        captchaMap[userId] = captcha
    }

    override fun contains(userId: Int): Boolean {
        return captchaMap.contains(userId)
    }

    override fun remove(userId: Int) {
        captchaMap.remove(userId)
    }

    override fun get(userId: Int): ResultOf.Captcha {
        return captchaMap[userId]!!
    }
}