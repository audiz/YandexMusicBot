package bot

import bot.telegram.Bot
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@SpringBootApplication
class BotApplication

fun main(args: Array<String>) {
	val ctx = runApplication<BotApplication>(*args)
	val bot = ctx.getBean("bot", Bot::class.java);
	val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
	botsApi.registerBot(bot)
}
