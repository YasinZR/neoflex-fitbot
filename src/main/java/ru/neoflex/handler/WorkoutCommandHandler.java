package ru.neoflex.handler;

import lombok.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.neoflex.model.User;
import ru.neoflex.model.WorkoutLog;
import ru.neoflex.service.OnboardingService;
import ru.neoflex.service.WorkoutService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ru.neoflex.util.BotResponseUtils.sendText;

@Component
@RequiredArgsConstructor
public class WorkoutCommandHandler {

    private final WorkoutService workoutService;
    private final OnboardingService onboardingService;

    // временное хранилище для состояния ввода тренировки
    private final Map<Long, PartialWorkout> workoutBuffer = new ConcurrentHashMap<>();

    public void handleAddWorkout(Update update) {
        long chatId = update.getMessage().getChatId();
        sendText(chatId, "Введите тип тренировки (например: Бег, Плавание, Велосипед):");
        workoutBuffer.put(chatId, new PartialWorkout()); // создаём пустой шаблон
    }

    public void handleTextStep(Update update) {
        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        PartialWorkout buffer = workoutBuffer.get(chatId);
        if (buffer == null) return;

        switch (buffer.getCurrentStep()) {
            case TYPE -> {
                buffer.setType(text);
                buffer.setCurrentStep(PartialWorkout.InputStep.DURATION);
                sendText(chatId, "Укажи продолжительность в минутах:");
            }
            case DURATION -> {
                try {
                    buffer.setDurationMin(Integer.parseInt(text));
                    buffer.setCurrentStep(PartialWorkout.InputStep.CALORIES);
                    sendText(chatId, "Сколько калорий сожжено?");
                } catch (NumberFormatException e) {
                    sendText(chatId, "Неверный формат. Введите число, например: 45");
                }
            }
            case CALORIES -> {
                try {
                    buffer.setCalories(Integer.parseInt(text));
                    User user = onboardingService.getUserByTelegramId(chatId);

                    WorkoutLog workout = WorkoutLog.builder()
                            .user(user)
                            .type(buffer.getType())
                            .durationMin(buffer.getDurationMin())
                            .calories(buffer.getCalories())
                            .timestamp(LocalDateTime.now())
                            .build();

                    workoutService.addWorkout(workout);
                    sendText(chatId, "Тренировка сохранена!");

                    workoutBuffer.remove(chatId);
                } catch (NumberFormatException e) {
                    sendText(chatId, "Неверный формат. Введите число, например: 300");
                }
            }
        }
    }


    @RequiredArgsConstructor
    @Getter
    @Setter
    private static class PartialWorkout {
        private String type;
        private Integer durationMin;
        private Integer calories;
        private InputStep currentStep = InputStep.TYPE;

        enum InputStep {
            TYPE, DURATION, CALORIES
        }

    }
}
