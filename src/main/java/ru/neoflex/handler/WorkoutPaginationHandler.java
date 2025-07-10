package ru.neoflex.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.neoflex.model.WorkoutLog;
import ru.neoflex.service.OnboardingService;
import ru.neoflex.service.WorkoutService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static ru.neoflex.util.BotResponseUtils.executeSafe;

@Component
@RequiredArgsConstructor
public class WorkoutPaginationHandler {

    private final WorkoutService workoutService;
    private final OnboardingService onboardingService;

    private static final int PAGE_SIZE = 5;

    public void handleListCommand(Update update, int page) {
        long chatId = update.hasCallbackQuery() ?
                update.getCallbackQuery().getMessage().getChatId() :
                update.getMessage().getChatId();

        Long userId = onboardingService.getUserByTelegramId(chatId).getId();
        List<WorkoutLog> workouts = workoutService.listWorkouts(userId);

        int from = page * PAGE_SIZE;
        int to = Math.min(from + PAGE_SIZE, workouts.size());
        if (from >= workouts.size()) {
            executeSafe(SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text("Тренировок на этой странице нет.")
                    .build());
            return;
        }

        List<WorkoutLog> pageItems = workouts.subList(from, to);
        StringBuilder text = new StringBuilder("🏋️‍♂️ Твои тренировки:\n\n");

        for (WorkoutLog w : pageItems) {
            text.append(String.format("📌 [%d] %s, %d мин, %d ккал (%s)\n",
                    w.getId(),
                    w.getType(),
                    w.getDurationMin(),
                    w.getCalories(),
                    w.getTimestamp().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))));
        }

        // Кнопки
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (WorkoutLog w : pageItems) {
            List<InlineKeyboardButton> row = List.of(
                    InlineKeyboardButton.builder().text("✏ Редактировать").callbackData("edit_" + w.getId()).build(),
                    InlineKeyboardButton.builder().text("🗑 Удалить").callbackData("delete_" + w.getId()).build()
            );
            keyboard.add(row);
        }

        // Навигация
        List<InlineKeyboardButton> navigation = new ArrayList<>();
        if (page > 0)
            navigation.add(InlineKeyboardButton.builder().text("⬅").callbackData("page_" + (page - 1)).build());
        if (to < workouts.size())
            navigation.add(InlineKeyboardButton.builder().text("➡").callbackData("page_" + (page + 1)).build());
        if (!navigation.isEmpty())
            keyboard.add(navigation);

        executeSafe(SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text.toString())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(keyboard).build())
                .build());
    }
}
