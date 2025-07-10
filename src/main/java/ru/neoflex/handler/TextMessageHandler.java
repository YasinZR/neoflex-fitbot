package ru.neoflex.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.neoflex.model.enums.OnboardingStep;
import ru.neoflex.model.User;
import ru.neoflex.service.OnboardingService;

import static ru.neoflex.util.BotResponseUtils.*;

@Component
@RequiredArgsConstructor
public class TextMessageHandler {

    private final OnboardingStepHandler onboardingStepHandler;
    private final OnboardingService onboardingService;
    private final MainMenuHandler mainMenuHandler;

    public void handle(Update update) {
        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        String userName = update.getMessage().getFrom().getFirstName();

        if (messageText.equals("/start")) {
            handleStartCommand(chatId, userName);
            return;
        }

        User user = onboardingService.getUserByTelegramId(chatId);
        OnboardingStep step = user.getOnboardingStep();

        if (step == OnboardingStep.CUSTOM_WEIGHT) {
            handleCustomWeight(chatId, messageText);
        } else if (step == OnboardingStep.CUSTOM_HEIGHT) {
            handleCustomHeight(chatId, messageText);
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
