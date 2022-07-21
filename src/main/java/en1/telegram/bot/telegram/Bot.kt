package en1.telegram.bot.telegram

import en1.common.EnvConfiguration
import en1.telegram.bot.telegram.service.CommandProcessor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand
import org.telegram.telegrambots.meta.api.objects.Update

/**
 * Bot startup configuration
 * */
@Component
class Bot(val envConfiguration: EnvConfiguration, val commandProcessor: CommandProcessor, botCommands: List<IBotCommand>): TelegramLongPollingCommandBot() {
    private val logger = LoggerFactory.getLogger(Bot::class.java)

    init {
        botCommands.forEach { register(it) }
        logger.info("Бот создан!")
    }

    override fun getBotUsername(): String {
        return envConfiguration.getBotUsername()
    }

    override fun getBotToken(): String {
        return envConfiguration.getBotToken()
    }

    /**
     * Handle non-command requests. E.g. text messages, files, callbacks...
     * */
    override fun processNonCommandUpdate(update: Update) {
        commandProcessor.processNonCommand(update, this)
    }
}