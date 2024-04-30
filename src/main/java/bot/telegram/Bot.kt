package bot.telegram

import bot.common.EnvConfiguration
import bot.telegram.service.CommandProcessorImpl
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand
import java.util.*

/**
 * Bot startup configuration
 * */
@Component
class Bot(val envConfiguration: EnvConfiguration,
          val commandProcessor: CommandProcessorImpl,
          messageSource: MessageSource,
          botCommands: List<IBotCommand>): TelegramLongPollingCommandBot() {
    private val logger = LoggerFactory.getLogger(Bot::class.java)

    init {
        botCommands.forEach { register(it) }
        val listCommand = listOf(
            BotCommand("download", messageSource.getMessage("main.button.download", null, Locale.getDefault())),
            BotCommand("daily", messageSource.getMessage("main.button.daily", null, Locale.getDefault())),
            BotCommand("playlists", messageSource.getMessage("main.button.playlists", null, Locale.getDefault())),
            BotCommand("help", messageSource.getMessage("main.button.help", null, Locale.getDefault()))
        )
        this.execute(SetMyCommands(listCommand, null, null))
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