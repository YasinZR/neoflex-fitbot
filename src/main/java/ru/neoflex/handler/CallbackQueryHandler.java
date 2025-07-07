package ru.neoflex.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.neoflex.model.OnboardingStep;
import ru.neoflex.service.OnboardingService;

import static ru.neoflex.util.BotResponseUtils.executeSafe;

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
            String gender = data.equals("gender_male") ? "Мужской" : "Женский";
            onboardingService.saveGender(chatId, gender);
            onboardingService.saveOnboardingStep(chatId, OnboardingStep.AGE);
            executeSafe(SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text("Отлично! Теперь выбери возраст:")
                    .replyMarkup(onboardingStepHandler.ageKeyboard())
                    .build());

        } else if (data.startsWith("age_")) {
            String ageRange = data.replace("age_", "");
            onboardingService.saveAgeRange(chatId, ageRange);
            onboardingService.saveOnboardingStep(chatId, OnboardingStep.WEIGHT);
            executeSafe(SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text("Теперь укажи свой вес относительно среднего (70 кг):")
                    .replyMarkup(onboardingStepHandler.weightKeyboard())
                    .build());

        } else if (data.startsWith("weight_")) {
            switch (data) {
                case "weight_minus5" -> onboardingService.saveWeight(chatId, "65");
                case "weight_plus5" -> onboardingService.saveWeight(chatId, "75");
                case "weight_custom" -> {
                    onboardingService.saveOnboardingStep(chatId, OnboardingStep.CUSTOM_WEIGHT);
                    executeSafe(SendMessage.builder()
                            .chatId(String.valueOf(chatId))
                            .text("Введите ваш вес вручную (в кг):")
                            .build());
                    return;
                }
            }
            onboardingService.saveOnboardingStep(chatId, OnboardingStep.HEIGHT);
            executeSafe(SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text("Теперь укажи рост относительно среднего (175 см):")
                    .replyMarkup(onboardingStepHandler.heightKeyboard())
                    .build());

        } else if (data.startsWith("height_")) {
            switch (data) {
                case "height_minus5" -> onboardingService.saveHeight(chatId, "170");
                case "height_plus5" -> onboardingService.saveHeight(chatId, "180");
                case "height_custom" -> {
                    onboardingService.saveOnboardingStep(chatId, OnboardingStep.CUSTOM_HEIGHT);
                    executeSafe(SendMessage.builder()
                            .chatId(String.valueOf(chatId))
                            .text("Введите ваш рост вручную (в см):")
                            .build());
                    return;
                }
            }
            onboardingService.saveOnboardingStep(chatId, OnboardingStep.ACTIVITY_LEVEL);
            executeSafe(SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text("Выбери уровень активности:")
                    .replyMarkup(onboardingStepHandler.activityLevelKeyboard())
                    .build());

        } else if (data.startsWith("activity_")) {
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
                executeSafe(SendMessage.builder()
                        .chatId(String.valueOf(chatId))
                        .text("Какая у тебя цель?")
                        .replyMarkup(onboardingStepHandler.goalKeyboard())
                        .build());
            }

        } else if (data.startsWith("goal_")) {
            String goal = switch (data) {
                case "goal_lose" -> "Похудеть";
                case "goal_maintain" -> "Поддерживать";
                case "goal_gain" -> "Набрать";
                default -> null;
            };
            if (goal != null) {
                onboardingService.saveGoal(chatId, goal);
                onboardingService.saveOnboardingStep(chatId, OnboardingStep.COMPLETE);
                executeSafe(SendMessage.builder()
                        .chatId(String.valueOf(chatId))
                        .text("✅ Профиль сохранён! Вот твоё главное меню:")
                        .build());
                executeSafe(mainMenuHandler.createMainMenu(chatId, userName));
            }
        }
    }
}
