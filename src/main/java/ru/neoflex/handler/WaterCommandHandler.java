package ru.neoflex.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.neoflex.model.User;
import ru.neoflex.service.OnboardingService;
import ru.neoflex.service.WaterService;
import ru.neoflex.util.NavigationMarkupFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ru.neoflex.util.BotResponseUtils.*;

@Component
@RequiredArgsConstructor
public class WaterCommandHandler {

    private final WaterService waterService;
    private final OnboardingService onboardingService;
    Map<Long, Boolean> pendingCustomVolume = new ConcurrentHashMap<>();

    public void handleWaterStats(Update update) {
        long chatId = update.getMessage().getChatId();
        User user = onboardingService.getUserByTelegramId(chatId);

        int goal = waterService.getDailyGoal(user.getTelegramId());
        int total = waterService.getDailyTotal(user.getTelegramId(), LocalDate.now());

        sendText(chatId, String.format("💧 Сегодня выпито: %d мл\n🎯 Цель: %d мл", total, goal));
    }

    public void handleSetWaterGoal(Update update, int goalMl) {
        long chatId = update.getMessage().getChatId();
        waterService.setDailyGoal(chatId, goalMl);
        sendText(chatId, "✅ Цель по воде обновлена: " + goalMl + " мл");
    }

    public void handleCallback(long chatId, String data) {
        if (data.equals("WATER_MENU")) {
            User user = onboardingService.getUserByTelegramId(chatId);
            int goal = user.getDailyWaterGoal() != null ? user.getDailyWaterGoal() : 2500;

            InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                    .keyboard(List.of(
                            List.of(
                                    button("💧 +200 мл", "WATER_ADD_200"),
                                    button("💧 +500 мл", "WATER_ADD_500"),
                                    button("➕ Другая…", "WATER_CUSTOM")
                            ),
                            List.of(
                                    button("➖100", "WATER_GOAL_MINUS"),
                                    button("🎯 Цель: " + goal + " мл", "WATER_GOAL_STATIC"),
                                    button("➕100", "WATER_GOAL_PLUS")
                            )
                    ))
                    .build();

            sendTextWithKeyboard(chatId, "Выбери действие:", markup);
        }

        if (data.startsWith("WATER_ADD_")) {
            int amount = Integer.parseInt(data.replace("WATER_ADD_", ""));
            waterService.addIntake(chatId, amount);
            sendText(chatId, "✅ Добавлено " + amount + " мл воды!");
        }

        if (data.equals("WATER_GOAL_PLUS")) {
            waterService.incrementGoal(chatId, 100);
            sendText(chatId, "🎯 Цель увеличена на 100 мл!");
        }

        if (data.equals("WATER_GOAL_MINUS")) {
            waterService.incrementGoal(chatId, -100);
            sendText(chatId, "🎯 Цель уменьшена на 100 мл!");
        }

        if (data.equals("WATER_CUSTOM")) {
            sendText(chatId, "Введите объём воды в мл (например: 350):");
            pendingCustomVolume.put(chatId, true);
        }
    }


    public void handleCustomVolume(Update update) {
        long chatId = update.getMessage().getChatId();
        if (!pendingCustomVolume.getOrDefault(chatId, false)) return;


        String text = update.getMessage().getText();
        try {
            int volume = Integer.parseInt(text);
            waterService.addIntake(chatId, volume);
            if (volume <= 0) {
                sendText(chatId, "Объём должен быть положительным числом.");
                return;
            }
            sendTextWithKeyboard(chatId, "✅ Добавлено " + volume + " мл воды.", NavigationMarkupFactory.navigationMarkup());
            pendingCustomVolume.remove(chatId); // сбрасываем флаг
        } catch (NumberFormatException e) {
            sendText(chatId, "Введите корректное число, например: 250");
        }
    }

}
