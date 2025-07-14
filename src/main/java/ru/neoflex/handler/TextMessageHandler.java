package ru.neoflex.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.neoflex.model.CalculatorProfile;
import ru.neoflex.model.enums.OnboardingStep;
import ru.neoflex.model.User;
import ru.neoflex.service.CalculatorService;
import ru.neoflex.service.OnboardingService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ru.neoflex.util.BotResponseUtils.*;

@Component
@RequiredArgsConstructor
public class TextMessageHandler {

    private final OnboardingStepHandler onboardingStepHandler;
    private final OnboardingService onboardingService;
    private final MainMenuHandler mainMenuHandler;
    private final CalculatorService calculatorService;
    private final Map<Long, Boolean> calculatorModeUsers = new ConcurrentHashMap<>();


    public void handle(Update update) {
        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        String userName = update.getMessage().getFrom().getFirstName();

        if (messageText.equals("/start")) {
            handleStartCommand(chatId, userName);
            return;
        }

        if (messageText.equals("/calculator")) {
            calculatorModeUsers.put(chatId, true);
            onboardingService.startOnboarding(chatId); // сбрасываем шаги
            sendTextWithKeyboard(chatId, "Давай рассчитаем калории. Укажи свой пол:", onboardingStepHandler.genderKeyboard());
            return;
        }

        User user = onboardingService.getUserByTelegramId(chatId);
        OnboardingStep step = user.getOnboardingStep();

        if (messageText.equals("/calories")) {
            CalculatorProfile profile = user.getCalculatorProfile();

            double calories = calculatorService.calculateStoredCalories(profile);
            sendText(chatId, "🔥 Твоя суточная потребность: *" + (int) calories + "* ккал");
            return;
        }

        if (step == OnboardingStep.CUSTOM_WEIGHT) {
            handleCustomWeight(chatId, messageText);
            return;
        }

        if (step == OnboardingStep.CUSTOM_HEIGHT) {
            handleCustomHeight(chatId, messageText);
            return;
        }

        if (step == OnboardingStep.GOAL) {
            onboardingService.saveGoal(chatId, messageText);

            if (calculatorModeUsers.getOrDefault(chatId, false)) {
                var profile = onboardingService.getUserByTelegramId(chatId).getCalculatorProfile();
                int calories = calculatorService.calculateCalories(
                        profile.getGender(),
                        profile.getAge(),
                        profile.getWeight(),
                        profile.getHeight(),
                        profile.getActivityLevel(),
                        profile.getGoal()
                );

                sendTextWithButtons(chatId,
                        "🔥 Твоя суточная потребность: " + calories + " ккал",
                        Map.of(
                                "✅ Сохранить в профиль", "SAVE_CALC_PROFILE",
                                "❌ Отменить", "CANCEL_CALC_PROFILE"
                        )
                );

                calculatorModeUsers.remove(chatId); // выходим из режима калькулятора
            } else {
                mainMenuHandler.showMenu(chatId); // обычный онбординг завершён
            }
        }
    }




    private void handleStartCommand(long chatId, String userName) {
        onboardingService.startOnboarding(chatId);
        sendTextWithKeyboard(
                chatId,
                "Привет, " + userName + "! Давай настроим твой профиль. Начнём с пола:",
                onboardingStepHandler.genderKeyboard()
        );
    }

    private void handleCustomWeight(long chatId, String messageText) {
        try {
            onboardingService.saveWeight(chatId, messageText);
            onboardingService.saveOnboardingStep(chatId, OnboardingStep.HEIGHT);
            sendTextWithKeyboard(
                    chatId,
                    "Теперь укажи рост относительно среднего (175 см):",
                    onboardingStepHandler.heightKeyboard()
            );
        } catch (NumberFormatException e) {
            sendText(chatId,"Некорректный формат. Введите вес числом, например: 73");
        }
    }

    private void handleCustomHeight(long chatId, String messageText) {
        try {
            onboardingService.saveHeight(chatId, messageText);
            onboardingService.saveOnboardingStep(chatId, OnboardingStep.ACTIVITY_LEVEL);
            sendTextWithKeyboard(
                    chatId,
                    "Выбери уровень активности:",
                    onboardingStepHandler.activityLevelKeyboard()

            );
        } catch (NumberFormatException e) {
            sendText(chatId,"Некорректный формат. Введите рост числом, например: 178");
        }
    }
}
