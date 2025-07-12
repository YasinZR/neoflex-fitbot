package ru.neoflex;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.neoflex.config.BotConfig;
import ru.neoflex.handler.*;
import ru.neoflex.util.BotResponseUtils;

@Slf4j
@Component
public class NeoflexFitBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final TextMessageHandler textMessageHandler;
    private final CallbackQueryHandler callbackQueryHandler;
    private final WorkoutCommandHandler workoutCommandHandler;
    private final WorkoutPaginationHandler workoutPaginationHandler;
    private final WorkoutEditHandler workoutEditHandler;



    public NeoflexFitBot(BotConfig config,
                         TextMessageHandler textMessageHandler,
                         CallbackQueryHandler callbackQueryHandler,
                         WorkoutCommandHandler workoutCommandHandler,
                         WorkoutEditHandler workoutEditHandler,
                         WorkoutPaginationHandler workoutPaginationHandler) {
        this.config = config;
        this.textMessageHandler = textMessageHandler;
        this.callbackQueryHandler = callbackQueryHandler;
        this.workoutCommandHandler = workoutCommandHandler;
        this.workoutPaginationHandler = workoutPaginationHandler;
        this.workoutEditHandler = workoutEditHandler;
        BotResponseUtils.init(this);
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();

            if (messageText.equals("/addWorkout")) {
                workoutCommandHandler.handleAddWorkout(update);
                return;
            } else if (messageText.equals("/listWorkouts")) {
                workoutPaginationHandler.handleListCommand(update, 0);
                return;
            } else {
                workoutCommandHandler.handleTextStep(update);
                workoutEditHandler.handleTextStep(update); // добавь это
                textMessageHandler.handle(update);
            }


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
