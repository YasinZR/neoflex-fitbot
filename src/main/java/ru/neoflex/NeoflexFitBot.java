package ru.neoflex;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.neoflex.config.BotConfig;
import ru.neoflex.handler.CallbackQueryHandler;
import ru.neoflex.handler.TextMessageHandler;
import ru.neoflex.util.BotResponseUtils;

@Slf4j
@Component
public class NeoflexFitBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final TextMessageHandler textMessageHandler;
    private final CallbackQueryHandler callbackQueryHandler;

    public NeoflexFitBot(BotConfig config,
                         TextMessageHandler textMessageHandler,
                         CallbackQueryHandler callbackQueryHandler) {
        this.config = config;
        this.textMessageHandler = textMessageHandler;
        this.callbackQueryHandler = callbackQueryHandler;
        BotResponseUtils.init(this); // ← инициализируем для executeSafe
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            textMessageHandler.handle(update);
        } else if (update.hasCallbackQuery()) {
            callbackQueryHandler.handle(update);
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
