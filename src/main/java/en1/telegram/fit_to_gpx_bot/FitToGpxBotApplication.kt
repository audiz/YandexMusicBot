package en1.telegram.fit_to_gpx_bot

import en1.telegram.fit_to_gpx_bot.telegram.Bot
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@SpringBootApplication(scanBasePackages = ["yandex", "en1"])
open class FitToGpxBotApplication

fun main(args: Array<String>) {
	val ctx = runApplication<FitToGpxBotApplication>(*args)
	val bot = ctx.getBean("bot", Bot::class.java);
	val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
	botsApi.registerBot(bot)
}
