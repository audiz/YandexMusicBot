package en1.telegram.bot.telegram.service

import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.meta.api.objects.Update

interface CommandProcessor {
    fun processNonCommand(update: Update, absSender: DefaultAbsSender)
}