package ru.neoflex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.neoflex.config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.neoflex.handler.MainMenuHandler;

@Component
public class NeoflexFitBot extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(NeoflexFitBot.class);
    private final BotConfig config;
    private final MainMenuHandler mainMenuHandler;

    public NeoflexFitBot(BotConfig config, MainMenuHandler mainMenuHandler) {
        this.config = config;
        this.mainMenuHandler = mainMenuHandler;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom().getFirstName();

            if (messageText.equals("/start")) {
                SendMessage message = mainMenuHandler.createMainMenu(chatId, userName);
                executeSafe(message);
            }
        }
    }

    private void executeSafe(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error sending message", e);
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }
}
