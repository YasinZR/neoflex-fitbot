package ru.neoflex.handler;

import lombok.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.neoflex.model.NutritionEntry;
import ru.neoflex.model.User;
import ru.neoflex.service.NutritionService;
import ru.neoflex.service.OnboardingService;

import static ru.neoflex.util.BotResponseUtils.sendButtons;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import static ru.neoflex.util.BotResponseUtils.sendText;

@Component
@RequiredArgsConstructor
public class NutritionCommandHandler {

    private final NutritionService nutritionService;
    private final OnboardingService onboardingService;


    private final Map<Long, PartialMeal> mealBuffer = new ConcurrentHashMap<>();

    public void handleAddMeal(Update update) {
        long chatId = update.getMessage().getChatId();
        sendText(chatId, "Выбери тип приёма пищи (например: завтрак, обед, ужин, перекус):");
        mealBuffer.put(chatId, new PartialMeal());
    }

    public void handleTextStep(Update update) {
        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        PartialMeal buffer = mealBuffer.get(chatId);
        if (buffer == null) return;

        User user = onboardingService.getUserByTelegramId(chatId);

        switch (buffer.getCurrentStep()) {
            case TYPE -> {
                buffer.setMealType(text);
                buffer.setCurrentStep(PartialMeal.InputStep.PROTEIN);
                sendText(chatId, "Сколько белков (г)?");
            }
            case PROTEIN -> {
                try {
                    buffer.setProtein(Double.parseDouble(text));
                    buffer.setCurrentStep(PartialMeal.InputStep.FAT);
                    sendText(chatId, "Сколько жиров (г)?");
                } catch (NumberFormatException e) {
                    sendText(chatId, "Введите число, например: 25.5");
                }
            }
            case FAT -> {
                try {
                    buffer.setFat(Double.parseDouble(text));
                    buffer.setCurrentStep(PartialMeal.InputStep.CARBS);
                    sendText(chatId, "Сколько углеводов (г)?");
                } catch (NumberFormatException e) {
                    sendText(chatId, "Введите число, например: 10.2");
                }
            }
            case CARBS -> {
                try {
                    buffer.setCarbs(Double.parseDouble(text));

                    if (buffer.getMealId() != null) {
                        // редактирование
                        NutritionEntry updated = NutritionEntry.builder()
                                .id(buffer.getMealId())
                                .user(user)
                                .mealType(buffer.getMealType())
                                .protein(buffer.getProtein())
                                .fat(buffer.getFat())
                                .carbs(buffer.getCarbs())
                                .timestamp(LocalDateTime.now())
                                .build();

                        nutritionService.updateMeal(updated);
                        sendText(chatId, "✅ Приём пищи обновлён!");
                    } else {
                        // добавление нового
                        NutritionEntry entry = NutritionEntry.builder()
                                .user(user)
                                .mealType(buffer.getMealType())
                                .protein(buffer.getProtein())
                                .fat(buffer.getFat())
                                .carbs(buffer.getCarbs())
                                .timestamp(LocalDateTime.now())
                                .build();

                        nutritionService.addMeal(entry);
                        sendText(chatId, "🍽 Приём пищи сохранён!");
                    }

                    mealBuffer.remove(chatId);
                } catch (NumberFormatException e) {
                    sendText(chatId, "Введите число, например: 70.0");
                }
            }

        }
    }

    public void handleTodayMeals(Update update) {
        long chatId = update.getMessage().getChatId();
        User user = onboardingService.getUserByTelegramId(chatId);

        LocalDate today = LocalDate.now();
        List<NutritionEntry> meals = nutritionService.getMealsByDate(user.getId(), today);

        if (meals.isEmpty()) {
            sendText(chatId, "🍽 Сегодня приёмов пищи ещё не было.");
            return;
        }

        double totalProtein = 0;
        double totalFat = 0;
        double totalCarbs = 0;

        StringBuilder details = new StringBuilder("🍛 Приёмы пищи за сегодня:\n\n");

        for (NutritionEntry meal : meals) {
            totalProtein += meal.getProtein();
            totalFat += meal.getFat();
            totalCarbs += meal.getCarbs();

            details.append(String.format("• [%d] %s в %s (Б: %.1fг, Ж: %.1fг, У: %.1fг)\n",
                    meal.getId(),
                    meal.getMealType(),
                    meal.getTimestamp().toLocalTime().withSecond(0).withNano(0),
                    meal.getProtein(), meal.getFat(), meal.getCarbs()));
        }

        details.append(String.format("\nИтого: Б: %.1fг, Ж: %.1fг, У: %.1fг", totalProtein, totalFat, totalCarbs));
        sendText(chatId, details.toString());
    }

    public void handleEditMeal(Update update, Long mealId) {
        long chatId = update.getMessage().getChatId();
        Optional<NutritionEntry> optionalEntry = nutritionService.getMealById(mealId);

        if (optionalEntry.isEmpty()) {
            sendText(chatId, "❌ Приём пищи не найден.");
            return;
        }

        NutritionEntry entry = optionalEntry.get();
        mealBuffer.put(chatId, new PartialMeal(entry));

        sendText(chatId, String.format("Редактируем приём [%d]. Введите тип (текущий: %s)",
                entry.getId(), entry.getMealType()));
    }

    public void handleDeleteMeal(Update update, Long mealId) {
        long chatId = update.getMessage().getChatId();
        Optional<NutritionEntry> optionalEntry = nutritionService.getMealById(mealId);

        if (optionalEntry.isEmpty()) {
            sendText(chatId, "❌ Приём пищи не найден.");
            return;
        }

        // показываем кнопки подтверждения
        sendButtons(chatId, "Вы уверены, что хотите удалить приём пищи?",
                Map.of(
                        "✅ Удалить", "CONFIRM_DEL_MEAL:" + mealId,
                        "❌ Отмена", "CANCEL_DEL_MEAL"
                ));
    }

    public void confirmDeleteMeal(long chatId, Long mealId) {
        Optional<NutritionEntry> optionalEntry = nutritionService.getMealById(mealId);

        if (optionalEntry.isEmpty()) {
            sendText(chatId, "❌ Приём пищи уже удалён или не найден.");
            return;
        }

        nutritionService.deleteMeal(mealId);
        sendText(chatId, "🗑 Приём пищи удалён.");
    }


    public void handleWeekMeals(Update update) {
        long chatId = update.getMessage().getChatId();
        User user = onboardingService.getUserByTelegramId(chatId);

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6); // последние 7 дней включая сегодня

        List<NutritionEntry> meals = nutritionService.getMealsForWeek(user.getId(), weekStart);

        if (meals.isEmpty()) {
            sendText(chatId, "🍽 За последние 7 дней приёмов пищи не найдено.");
            return;
        }

        Map<LocalDate, DailyTotal> dailyTotals = new TreeMap<>();

        for (NutritionEntry meal : meals) {
            LocalDate date = meal.getTimestamp().toLocalDate();
            dailyTotals.putIfAbsent(date, new DailyTotal());
            dailyTotals.get(date).add(meal);
        }

        StringBuilder message = new StringBuilder("🍱 Приёмы пищи за последние 7 дней:\n\n");

        for (Map.Entry<LocalDate, DailyTotal> entry : dailyTotals.entrySet()) {
            LocalDate date = entry.getKey();
            DailyTotal total = entry.getValue();
            message.append(String.format("%s: Б: %.1f, Ж: %.1f, У: %.1f\n",
                    date, total.protein, total.fat, total.carbs));
        }

        sendText(chatId, message.toString());
    }



    @Getter
    private static class DailyTotal {
        private double protein = 0;
        private double fat = 0;
        private double carbs = 0;

        void add(NutritionEntry entry) {
            this.protein += entry.getProtein();
            this.fat += entry.getFat();
            this.carbs += entry.getCarbs();
        }
    }



    @RequiredArgsConstructor
    @Getter
    @Setter
    private static class PartialMeal {
        private String mealType;
        private Double protein;
        private Double fat;
        private Double carbs;
        private Long mealId;
        private InputStep currentStep = InputStep.TYPE;

        public PartialMeal(NutritionEntry entry) {
            this.mealId = entry.getId();
            this.mealType = entry.getMealType();
            this.protein = entry.getProtein();
            this.fat = entry.getFat();
            this.carbs = entry.getCarbs();
            this.currentStep = InputStep.TYPE;
        }


        enum InputStep {
            TYPE, PROTEIN, FAT, CARBS
        }
    }
}
