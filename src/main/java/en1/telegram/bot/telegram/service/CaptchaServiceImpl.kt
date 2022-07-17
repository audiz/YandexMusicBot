package en1.telegram.bot.telegram.service

import en1.common.ResultOf
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class CaptchaServiceImpl : CaptchaService {
    private val captchaMap = ConcurrentHashMap<Long, ResultOf.Captcha>()
    override fun put(userId: Long, captcha: ResultOf.Captcha) {
        captchaMap[userId] = captcha
    }
    override fun containsKey(userId: Long): Boolean {
        return captchaMap.containsKey(userId)
    }
    override fun remove(userId: Long) {
        captchaMap.remove(userId)
    }
    override fun get(userId: Long): ResultOf.Captcha {
        return captchaMap[userId]!!
    }
}