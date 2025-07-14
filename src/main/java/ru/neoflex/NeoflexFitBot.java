package ru.neoflex;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.neoflex.config.BotConfig;
import ru.neoflex.handler.*;
import ru.neoflex.util.BotResponseUtils;

import static ru.neoflex.util.BotResponseUtils.sendText;

@Slf4j
@Component
public class NeoflexFitBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final TextMessageHandler textMessageHandler;
    private final CallbackQueryHandler callbackQueryHandler;
    private final WorkoutCommandHandler workoutCommandHandler;
    private final WorkoutPaginationHandler workoutPaginationHandler;
    private final WorkoutEditHandler workoutEditHandler;
    private final NutritionCommandHandler nutritionCommandHandler;
    private final WaterCommandHandler waterCommandHandler;
    private final MainMenuHandler mainMenuHandler;
    private Long extractId(String messageText) {
        try {
            String[] parts = messageText.split(" ");
            if (parts.length > 1) return Long.parseLong(parts[1]);
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    public NeoflexFitBot(BotConfig config,
                         TextMessageHandler textMessageHandler,
                         CallbackQueryHandler callbackQueryHandler,
                         WorkoutCommandHandler workoutCommandHandler,
                         WorkoutEditHandler workoutEditHandler,
                         NutritionCommandHandler nutritionCommandHandler,
                         WorkoutPaginationHandler workoutPaginationHandler,
                         WaterCommandHandler waterCommandHandler,
                         MainMenuHandler mainMenuHandler // <== добавлено
    ) {
        this.config = config;
        this.textMessageHandler = textMessageHandler;
        this.callbackQueryHandler = callbackQueryHandler;
        this.workoutCommandHandler = workoutCommandHandler;
        this.workoutPaginationHandler = workoutPaginationHandler;
        this.workoutEditHandler = workoutEditHandler;
        this.nutritionCommandHandler = nutritionCommandHandler;
        this.waterCommandHandler = waterCommandHandler;
        this.mainMenuHandler = mainMenuHandler; // <== добавлено

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
            } else if (messageText.equals("/addMeal")) {
                nutritionCommandHandler.handleAddMeal(update);
                return;
            } else if (messageText.equals("/todayMeals")) {
                nutritionCommandHandler.handleTodayMeals(update);
                return;
            } else if (messageText.equals("/weekMeals")) {
                nutritionCommandHandler.handleWeekMeals(update);
                return;
            } else if (messageText.equals("/waterStats")) {
                waterCommandHandler.handleWaterStats(update);
                return;
            } else if (messageText.startsWith("/setWaterGoal")) {
                try {
                    int goal = Integer.parseInt(messageText.replace("/setWaterGoal", "").trim());
                    waterCommandHandler.handleSetWaterGoal(update, goal);
                } catch (NumberFormatException e) {
                    sendText(update.getMessage().getChatId(), "Введите число после команды. Например: /setWaterGoal 2500");
                }
                return;
            } else if (messageText.startsWith("/editMeal")) {
                Long id = extractId(messageText);
                if (id != null) {
                    nutritionCommandHandler.handleEditMeal(update, id);
                }
                return;
            } else if (messageText.startsWith("/delMeal")) {
                Long id = extractId(messageText);
                if (id != null) {
                    nutritionCommandHandler.handleDeleteMeal(update, id);
                }
                return;
            }

            workoutCommandHandler.handleTextStep(update);
            workoutEditHandler.handleTextStep(update);
            nutritionCommandHandler.handleTextStep(update);
            waterCommandHandler.handleCustomVolume(update);
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
