package en1.telegram.bot.utils

import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User

object Utils {
    /**
     * Формирование имени пользователя
     * @param msg сообщение
     */
    fun getUserName(msg: Message): String {
        return getUserName(msg.from)
    }

    /**
     * Формирование имени пользователя. Если заполнен никнейм, используем его. Если нет - используем фамилию и имя
     * @param user пользователь
     */
    fun getUserName(user: User): String {
        return if (user.userName != null) user.userName else String.format("%s %s", user.lastName, user.firstName)
    }
}