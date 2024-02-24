package en1.common

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import kotlin.system.exitProcess

/**
 * Read system environment variables such as BOT name and token. The list of allowed users(to use bot) and yandex cookies(Session_id)
 * */
@Configuration
@PropertySource("classpath:application.properties")
class EnvConfiguration {
    private val logger = LoggerFactory.getLogger(EnvConfiguration::class.java)

    @Value("\${bot.name}")
    private lateinit var bName: String
    @Value("\${bot.token}")
    private lateinit var bToken: String
    @Value("\${yandex.cookie}")
    private lateinit var yCookie: String
    @Value("\${allowed.users}")
    private lateinit var aUsers: String

    fun getBotUsername(): String {
        return bName
    }

    fun getBotToken(): String {
        if (bToken.isNullOrBlank()) {
            println("Property bot.token not exists, exit. Check your application.properties")
            //exitProcess(1)
        }
        return bToken
    }

    fun getAllowedUsers(): List<String> {
        return aUsers.split(",")
    }

    fun getYandexCookie(): String? {
        return yCookie
    }

    fun setYandexCookie(yandexCookie: String) {
        this.yCookie = yandexCookie
    }

}