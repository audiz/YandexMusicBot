package en1.common

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import kotlin.system.exitProcess

/**
 * Read system environment variables such as BOT name and token. The list of allowed users(to use bot) and yandex cookies(Session_id)
 * */
@Configuration
class EnvConfiguration {
    private val logger = LoggerFactory.getLogger(EnvConfiguration::class.java)

    private val yandexCookie: String?
    private val botUsername: String
    private val botToken: String
    private var allowedUsers: List<String> = listOf()

    init {
        val getenv = System.getenv()
        yandexCookie = getenv!!["YANDEX_COOKIE"]
        if (!getenv.containsKey("BOT_NAME") || !getenv.containsKey("BOT_TOKEN")) {
            logger.error("Environment variables BOT_NAME and BOT_TOKEN must exist")
            exitProcess(1)
        }
        botUsername = getenv["BOT_NAME"]!!
        botToken = getenv["BOT_TOKEN"]!!
        allowedUsers = getenv["ALLOWED_USERS"]?.split(",") ?: listOf()
    }

    fun getBotUsername(): String {
        return botUsername
    }

    fun getBotToken(): String {
        return botToken
    }

    fun getAllowedUsers(): List<String> {
        return allowedUsers
    }

    fun getYandexCookie(): String? {
        return yandexCookie
    }
}