package en1.common

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import javax.annotation.PostConstruct

/**
 * Read system environment variables such as BOT name and token. The list of allowed users(to use bot) and yandex cookies(Session_id)
 * */
@Configuration
@PropertySource("classpath:application.properties")
class EnvConfiguration {

    @Value("\${bot.name}")
    private lateinit var bName: String
    @Value("\${bot.token}")
    private lateinit var bToken: String
    @Value("\${yandex.cookie}")
    private lateinit var yCookie: String
    @Value("\${allowed.users}")
    private lateinit var aUsers: String

    @PostConstruct
    fun construct() {
        if (bToken.isBlank()) {
            println("Property bot.token not exists, exit. Check your application.properties")
            throw RuntimeException("Property bot.token not exists, exit. Check your application.properties")
        }
    }

    fun getBotUsername(): String {
        return bName
    }

    fun getBotToken(): String {
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