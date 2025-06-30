package ru.neoflex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import jakarta.annotation.PostConstruct;


@SpringBootApplication
public class NeoflexFitBotApplication {

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
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(bot);
            System.out.println("✅ Бот запущен и зарегистрирован");
        } catch (TelegramApiException e) {
            System.err.println("❌ Ошибка запуска бота");
            e.printStackTrace();
        }
    }
}
