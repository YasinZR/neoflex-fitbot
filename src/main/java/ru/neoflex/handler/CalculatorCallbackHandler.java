package ru.neoflex.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.neoflex.service.OnboardingService;
import ru.neoflex.handler.MainMenuHandler;

import static ru.neoflex.util.BotResponseUtils.sendText;

@Component
@RequiredArgsConstructor
public class CalculatorCallbackHandler {

    private final OnboardingService onboardingService;
    private final MainMenuHandler mainMenuHandler;

    public void handle(long chatId, String data) {
        switch (data) {
            case "SAVE_CALC_PROFILE" -> {
                onboardingService.completeOnboarding(chatId);
                sendText(chatId, "✅ Профиль сохранён. Отлично поработали!");
                mainMenuHandler.showMenu(chatId);
            }
            case "CANCEL_CALC_PROFILE" -> {
                sendText(chatId, "❌ Калькулятор отменён. Если хочешь, можешь пройти позже.");
                mainMenuHandler.showMenu(chatId);
            }
        }
    }
}
