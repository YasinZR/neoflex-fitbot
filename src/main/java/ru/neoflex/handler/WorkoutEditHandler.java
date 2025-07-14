package ru.neoflex.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.neoflex.model.WorkoutLog;
import ru.neoflex.service.WorkoutService;
import ru.neoflex.util.NavigationMarkupFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import static ru.neoflex.util.BotResponseUtils.sendTextWithKeyboard;
import static ru.neoflex.util.BotResponseUtils.sendText;

@Component
@RequiredArgsConstructor
public class WorkoutEditHandler {

    private final WorkoutService workoutService;

    private final Map<Long, EditingState> editBuffer = new ConcurrentHashMap<>();

    public void handleEditRequest(Update update, Long workoutId) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        Optional<WorkoutLog> optionalWorkout = workoutService.getWorkoutById(workoutId);
        if (optionalWorkout.isEmpty()) {
            sendText(chatId, "❌ Тренировка не найдена.");
            return;
        }
        WorkoutLog workout = optionalWorkout.get();


        editBuffer.put(chatId, new EditingState(workoutId, workout, EditField.TYPE));

        sendText(chatId, "Редактируем тренировку [" + workoutId + "]. Введите новый *тип* тренировки (текущий: " + workout.getType() + ")");
    }

    public void handleTextStep(Update update) {
        long chatId = update.getMessage().getChatId();
        String input = update.getMessage().getText();

        EditingState state = editBuffer.get(chatId);
        if (state == null) return;

        switch (state.getCurrentField()) {
            case TYPE -> {
                state.workout.setType(input);
                state.setCurrentField(EditField.DURATION);
                sendText(chatId, "Теперь укажи новую *длительность* тренировки в минутах (текущая: " + state.workout.getDurationMin() + ")");
            }
            case DURATION -> {
                try {
                    state.workout.setDurationMin(Integer.parseInt(input));
                    state.setCurrentField(EditField.CALORIES);
                    sendText(chatId, "Теперь укажи новые *калории* (текущие: " + state.workout.getCalories() + ")");
                } catch (NumberFormatException e) {
                    sendText(chatId, "Неверный формат. Введите число.");
                }
            }
            case CALORIES -> {
                try {
                    state.workout.setCalories(Integer.parseInt(input));
                    workoutService.updateWorkout(state.workout);
                    editBuffer.remove(chatId);
                    sendTextWithKeyboard(chatId, "✅ Тренировка успешно обновлена!", NavigationMarkupFactory.navigationMarkup());
                } catch (NumberFormatException e) {
                    sendText(chatId, "Неверный формат. Введите число.");
                }
            }
        }
    }

    enum EditField {
        TYPE, DURATION, CALORIES
    }

    static class EditingState {
        private Long workoutId;
        private WorkoutLog workout;
        private EditField currentField;

        public EditingState(Long workoutId, WorkoutLog workout, EditField currentField) {
            this.workoutId = workoutId;
            this.workout = workout;
            this.currentField = currentField;
        }

        public Long getWorkoutId() {
            return workoutId;
        }

        public WorkoutLog getWorkout() {
            return workout;
        }

        public EditField getCurrentField() {
            return currentField;
        }

        public void setCurrentField(EditField currentField) {
            this.currentField = currentField;
        }
    }

}
