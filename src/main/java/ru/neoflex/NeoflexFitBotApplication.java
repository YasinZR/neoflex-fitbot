package ru.neoflex;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class NeoflexFitBotApplication {

    private static final Logger log = LoggerFactory.getLogger(NeoflexFitBotApplication.class);

    private final NeoflexFitBot bot;

    public NeoflexFitBotApplication(NeoflexFitBot bot) {
        this.bot = bot;
    }

    public static void main(String[] args) {
        SpringApplication.run(NeoflexFitBotApplication.class, args);
    }

    @PostConstruct
    public void start() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
            log.info("The bot has been successfully launched and registered.");
        } catch (TelegramApiException e) {
            log.error("Error registering bot", e);
        }
    }
}
