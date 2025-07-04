package ru.neoflex.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {

    private final Dotenv dotenv = Dotenv.load();

    public String getBotName() {
        return dotenv.get("BOT_NAME");
    }

    public String getToken() {
        return dotenv.get("BOT_TOKEN");
    }
}
