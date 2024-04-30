package bot.telegram.commands

import bot.common.EnvConfiguration
import bot.common.Utils
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import java.io.File
import java.util.*

/**
 * Команда "Авторизация в яндекс"
 */
@Component
class LoginCommand(val envConfiguration: EnvConfiguration, val messageSource: MessageSource) : ServiceCommand("login", "Авторизация в яндекс") {
    private val logger = LoggerFactory.getLogger(LoginCommand::class.java)
    override fun execute(absSender: AbsSender, user: User, chat: Chat, strings: Array<String>) {
        val userName: String = Utils.getUserName(user)

        if (!envConfiguration.getAllowedUsers().contains(user.id.toString())) {
            sendAnswer(absSender, chat.id, commandIdentifier, userName, messageSource.getMessage("bot.command.login.not-allowed", null, Locale.getDefault()))
        } else {
            if (strings.isNotEmpty()) {
                val yandexTextEnv = "YANDEX_COOKIE=\"${strings[0]}\""
                File("yandex_cookies.env").writeText(yandexTextEnv)
                envConfiguration.setYandexCookie(strings[0])
                sendAnswer(absSender, chat.id, commandIdentifier, userName, messageSource.getMessage("bot.command.login.executed", null, Locale.getDefault()))
            } else {
                sendAnswer(absSender, chat.id, commandIdentifier, userName, messageSource.getMessage("bot.command.login", null, Locale.getDefault()))
                //sendAnswer(absSender, chat.id, commandIdentifier, userName, """Login command /login Session_id=CUTCUTCUT""".trimIndent())
            }
        }
    }
}