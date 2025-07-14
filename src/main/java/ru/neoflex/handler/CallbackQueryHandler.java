package ru.neoflex.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.neoflex.model.enums.OnboardingStep;
import ru.neoflex.service.OnboardingService;
import ru.neoflex.service.WorkoutService;
import java.util.List;
import static ru.neoflex.util.BotResponseUtils.*;

@Component
@RequiredArgsConstructor
public class CallbackQueryHandler {

    private final OnboardingStepHandler onboardingStepHandler;
    private final MainMenuHandler mainMenuHandler;
    private final OnboardingService onboardingService;
    private final WorkoutService workoutService;
    private final WorkoutPaginationHandler workoutPaginationHandler;
    private final WorkoutEditHandler workoutEditHandler;
    private final NutritionCommandHandler nutritionCommandHandler;
    private final WaterCommandHandler waterCommandHandler;
    private final CalculatorCallbackHandler calculatorCallbackHandler;
    private final MainMenuCallbackHandler mainMenuCallbackHandler;
    private final NavigationButtonHandler navigationButtonHandler;



    public void handle(Update update) {
        String data = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        String userName = update.getCallbackQuery().getFrom().getFirstName();

        if (data.startsWith("MENU_")) {
            mainMenuCallbackHandler.handle(update.getCallbackQuery());
            return;
        }
            
        if (data.equals("NAV_BACK") || data.equals("NAV_MENU")) {
            navigationButtonHandler.handle(update.getCallbackQuery());
            return;
        }

        if (data.startsWith("edit_")) {
            Long workoutId = Long.parseLong(data.replace("edit_", ""));
            workoutEditHandler.handleEditRequest(update, workoutId);
            return;
        }

        if (data.startsWith("delete_")) {
            Long workoutId = Long.parseLong(data.replace("delete_", ""));
            InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                    .keyboardRow(List.of(
                            InlineKeyboardButton.builder().text("✅ Подтвердить").callbackData("confirm_delete_" + workoutId).build(),
                            InlineKeyboardButton.builder().text("❌ Отмена").callbackData("cancel_delete").build()
                    ))
                    .build();

            executeSafe(SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text("Ты точно хочешь удалить эту тренировку?")
                    .replyMarkup(markup)
                    .build());
            return;
        }

        if (data.startsWith("CONFIRM_DEL_MEAL:")) {
            Long mealId = Long.parseLong(data.replace("CONFIRM_DEL_MEAL:", ""));
            nutritionCommandHandler.confirmDeleteMeal(chatId, mealId);
        } else if (data.equals("CANCEL_DEL_MEAL")) {
            sendText(chatId, "❌ Удаление отменено.");
        }

        if (data.startsWith("confirm_delete_")) {
            Long workoutId = Long.parseLong(data.replace("confirm_delete_", ""));
            workoutService.deleteWorkout(workoutId);
            sendText(chatId, "✅ Тренировка удалена.");
            return;
        }

        if (data.equals("cancel_delete")) {
            sendText(chatId, "❌ Удаление отменено.");
            return;
        }

        if (data.startsWith("gender_")) {
            handleGenderCallback(chatId, data);
        } else if (data.startsWith("age_")) {
            handleAgeCallback(chatId, data);
        } else if (data.startsWith("weight_")) {
            handleWeightCallback(chatId, data);
        } else if (data.startsWith("height_")) {
            handleHeightCallback(chatId, data);
        } else if (data.startsWith("activity_")) {
            handleActivityCallback(chatId, data);
        } else if (data.startsWith("goal_")) {
            handleGoalCallback(chatId, data, userName);
        }
        if (data.startsWith("page_")) {
            int page = Integer.parseInt(data.replace("page_", ""));
            workoutPaginationHandler.handleListCommand(update, page);
        }
        if (data.startsWith("WATER_")) {
            waterCommandHandler.handleCallback(chatId, data);
        }
        if (data.equals("SAVE_CALC_PROFILE") || data.equals("CANCEL_CALC_PROFILE")) {
            calculatorCallbackHandler.handle(chatId, data);
            return;
        }
    }

    private void handleGenderCallback(long chatId, String data) {
        String gender = data.equals("gender_male") ? "Мужской" : "Женский";
        onboardingService.saveGender(chatId, gender);
        onboardingService.saveOnboardingStep(chatId, OnboardingStep.AGE);
        sendTextWithKeyboard(
                chatId,
                "Отлично! Теперь выбери возраст:",
                onboardingStepHandler.ageKeyboard());
   }

    private void handleAgeCallback(long chatId, String data) {
        String ageRange = data.replace("age_", "");
        onboardingService.saveAgeRange(chatId, ageRange);
        onboardingService.saveOnboardingStep(chatId, OnboardingStep.WEIGHT);
        sendTextWithKeyboard(
                chatId,
                "Теперь укажи свой вес относительно среднего (70 кг):",
                onboardingStepHandler.weightKeyboard());
    }

    private void handleWeightCallback(long chatId, String data) {
        switch (data) {
            case "weight_minus5" -> onboardingService.saveWeight(chatId, "65");
            case "weight_plus5" -> onboardingService.saveWeight(chatId, "75");
            case "weight_custom" -> {
                onboardingService.saveOnboardingStep(chatId, OnboardingStep.CUSTOM_WEIGHT);
                sendText(chatId, "Введите ваш вес вручную (в кг 80 ):");
                return;

            }
        }
        onboardingService.saveOnboardingStep(chatId, OnboardingStep.HEIGHT);
        sendTextWithKeyboard(
                chatId,
                "Теперь укажи рост относительно среднего (175 см):",
                onboardingStepHandler.heightKeyboard()
        );
    }

    private void handleHeightCallback(long chatId, String data) {
        switch (data) {
            case "height_minus5" -> onboardingService.saveHeight(chatId, "170");
            case "height_plus5" -> onboardingService.saveHeight(chatId, "180");
            case "height_custom" -> {
                onboardingService.saveOnboardingStep(chatId, OnboardingStep.CUSTOM_HEIGHT);
                sendText(chatId,"Введите ваш рост вручную (в см 190):");
                return;
            }
        }
        onboardingService.saveOnboardingStep(chatId, OnboardingStep.ACTIVITY_LEVEL);
        sendTextWithKeyboard(
                chatId,
                "Выбери уровень активности:",
                onboardingStepHandler.activityLevelKeyboard()
        );
    }

    private void handleActivityCallback(long chatId, String data) {
        String level = switch (data) {
            case "activity_low" -> "Низкий";
            case "activity_medium" -> "Средний";
            case "activity_high" -> "Высокий";
            case "activity_pro" -> "Профи";
            default -> null;
        };
        if (level != null) {
            onboardingService.saveActivityLevel(chatId, level);
            onboardingService.saveOnboardingStep(chatId, OnboardingStep.GOAL);
            sendTextWithKeyboard(chatId,"Какая у тебя цель?",onboardingStepHandler.goalKeyboard());
        }
    }

    private void handleGoalCallback(long chatId, String data, String userName) {
        String goal = switch (data) {
            case "goal_lose" -> "Похудеть";
            case "goal_maintain" -> "Поддерживать";
            case "goal_gain" -> "Набрать";
            default -> null;
        };
        if (goal != null) {
            onboardingService.saveGoal(chatId, goal);
            onboardingService.calculateAndShowCalories(chatId);

        }
    }
}
