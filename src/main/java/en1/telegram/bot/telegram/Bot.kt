package en1.telegram.bot.telegram

import en1.common.EnvConfiguration
import en1.telegram.bot.telegram.commands.service.DailyPlaylistCommand
import en1.telegram.bot.telegram.commands.service.HelpCommand
import en1.telegram.bot.telegram.commands.service.StartCommand
import en1.telegram.bot.telegram.music.CallbackMusicActions
import en1.telegram.bot.telegram.service.CommandProcessor
import en1.telegram.bot.telegram.service.MessageSender
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot
import org.telegram.telegrambots.meta.api.objects.Update

/**
 * Bot startup configuration
 * */
@Component
class Bot(val envConfiguration: EnvConfiguration, val commandProcessor: CommandProcessor,
          musicService: CallbackMusicActions, messageSender: MessageSender): TelegramLongPollingCommandBot() {
    private val logger = LoggerFactory.getLogger(Bot::class.java)
    init {
        register(StartCommand("start", "Начало"))
        register(HelpCommand("help", "Помощь"))
        register(DailyPlaylistCommand("daily", "Daily playlist", musicService, messageSender))
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