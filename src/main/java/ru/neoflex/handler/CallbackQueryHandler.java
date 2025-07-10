package ru.neoflex.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.neoflex.model.enums.OnboardingStep;
import ru.neoflex.service.OnboardingService;

import static ru.neoflex.util.BotResponseUtils.*;

@Component
@RequiredArgsConstructor
public class CallbackQueryHandler {

    private final OnboardingStepHandler onboardingStepHandler;
    private final MainMenuHandler mainMenuHandler;
    private final OnboardingService onboardingService;

    public void handle(Update update) {
        String data = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        String userName = update.getCallbackQuery().getFrom().getFirstName();

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
            onboardingService.saveOnboardingStep(chatId, OnboardingStep.COMPLETE);
            sendText(chatId,"✅ Профиль сохранён! Вот твоё главное меню:");
            executeSafe(mainMenuHandler.createMainMenu(chatId, userName));
        }
    }
}
