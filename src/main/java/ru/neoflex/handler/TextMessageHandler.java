package ru.neoflex.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.neoflex.model.OnboardingStep;
import ru.neoflex.model.User;
import ru.neoflex.service.OnboardingService;

import static ru.neoflex.util.BotResponseUtils.executeSafe;

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
            onboardingService.startOnboarding(chatId);
            SendMessage welcome = SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text("Привет, " + userName + "! Давай настроим твой профиль. Начнём с пола:")
                    .replyMarkup(onboardingStepHandler.genderKeyboard())
                    .build();
            executeSafe(welcome);
            return;
        }

        User user = onboardingService.getUserByTelegramId(chatId);
        OnboardingStep step = user.getOnboardingStep();

        if (step == OnboardingStep.CUSTOM_WEIGHT) {
            try {
                onboardingService.saveWeight(chatId, messageText);
                onboardingService.saveOnboardingStep(chatId, OnboardingStep.HEIGHT);
                executeSafe(SendMessage.builder()
                        .chatId(String.valueOf(chatId))
                        .text("Теперь укажи рост относительно среднего (175 см):")
                        .replyMarkup(onboardingStepHandler.heightKeyboard())
                        .build());
            } catch (NumberFormatException e) {
                executeSafe(SendMessage.builder()
                        .chatId(String.valueOf(chatId))
                        .text("Некорректный формат. Введите вес числом, например: 73")
                        .build());
            }
        } else if (step == OnboardingStep.CUSTOM_HEIGHT) {
            try {
                onboardingService.saveHeight(chatId, messageText);
                onboardingService.saveOnboardingStep(chatId, OnboardingStep.ACTIVITY_LEVEL);
                executeSafe(SendMessage.builder()
                        .chatId(String.valueOf(chatId))
                        .text("Выбери уровень активности:")
                        .replyMarkup(onboardingStepHandler.activityLevelKeyboard())
                        .build());
            } catch (NumberFormatException e) {
                executeSafe(SendMessage.builder()
                        .chatId(String.valueOf(chatId))
                        .text("Некорректный формат. Введите рост числом, например: 178")
                        .build());
            }
        }
    }
}
