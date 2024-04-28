package bot.telegram.configuration

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import java.nio.charset.StandardCharsets
import java.util.*


@Configuration
class BotConfig {

    @Bean("messageSource")
    fun messageSource(): MessageSource? {
        val messageSource = ResourceBundleMessageSource()
        messageSource.setBasenames("lang/messages")
        messageSource.setDefaultEncoding(StandardCharsets.ISO_8859_1.name())
        messageSource.setDefaultLocale(Locale("ru"))
        return messageSource
    }
}