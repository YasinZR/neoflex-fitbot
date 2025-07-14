package ru.neoflex.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.neoflex.model.CalculatorProfile;
import ru.neoflex.model.User;
import ru.neoflex.service.OnboardingService;
import ru.neoflex.util.BotResponseUtils;

import static ru.neoflex.util.BotResponseUtils.sendText;

@Component
@RequiredArgsConstructor
public class MainMenuCallbackHandler {

    private final MainMenuHandler mainMenuHandler;
    private final WaterCommandHandler waterCommandHandler;
    private final OnboardingService onboardingService;

    public void handle(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        Message message = callbackQuery.getMessage();
        long chatId = message.getChatId();

        switch (data) {
            case "MENU_WORKOUT" -> {
                String msg = """
                    🏋️‍♂️ Команды тренировок:
                    /addWorkout — добавить тренировку
                    /listWorkouts — список тренировок
                    """;
                sendText(chatId, msg);
            }
            case "MENU_NUTRITION" -> {
                String msg = """
                    🍽 Команды питания:
                    /addMeal — добавить приём пищи
                    /todayMeals — приёмы за сегодня
                    /weekMeals — приёмы за 7 дней
                    /editMeal [id] — редактировать
                    /delMeal [id] — удалить
                    """;
                sendText(chatId, msg);
            }
            case "MENU_WATER" -> {
                String msg = """
                💧 Вода:
                /waterStats — статистика за день
                /setWaterGoal — установить цель
                """;
                sendText(chatId, msg);
                waterCommandHandler.handleCallback(chatId, "WATER_MENU");
            }

            case "MENU_CALCULATOR" -> {
                String msg = """
                    🧮 Калькулятор калорий:
                    /calculator — пересчитать с начала
                    /calories — показать сохранённый результат
                    """;
                sendText(chatId, msg);
            }
            case "MENU_STATS" -> {
                sendText(chatId, "📊 Статистика будет доступна позже.");
            }
            case "MENU_PROFILE" -> {
                User user = onboardingService.getUserByTelegramId(chatId);
                CalculatorProfile profile = user.getCalculatorProfile();

                String msg = String.format("""
                    👤 Твой профиль:
                    Пол: %s
                    Возраст: %d
                    Вес: %.1f кг
                    Рост: %.1f см
                    Активность: %s
                    Цель: %s
                    """,
                        profile.getGender(),
                        profile.getAge(),
                        profile.getWeight(),
                        profile.getHeight(),
                        profile.getActivityLevel(),
                        profile.getGoal()
                );
                sendText(chatId, msg);
            }
            case "MENU_HELP" -> {
                String msg = """
                    ❓ Доступные команды:

                    🏋️‍♂️ Тренировки:
                    /addWorkout, /listWorkouts, /editWorkout, /deleteWorkout

                    🍽 Питание:
                    /addMeal, /todayMeals, /weekMeals, /editMeal, /delMeal

                    💧 Вода:
                    /waterStats, /setWaterGoal

                    🧮 Калькулятор:
                    /calculator, /calories

                    📌 Вопросы? Пиши сюда --> @Yasin_aux.
                    """;
                sendText(chatId, msg);
            }
        }
    }

}
