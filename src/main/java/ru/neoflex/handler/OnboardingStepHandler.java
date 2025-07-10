package ru.neoflex.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.neoflex.model.enums.AgeOption;
import ru.neoflex.model.enums.ActivityLevelOption;


import java.util.List;

@Component
public class OnboardingStepHandler {

    public InlineKeyboardMarkup genderKeyboard() {
        return createKeyboard(List.of(
                List.of(new ButtonData("Мужской", "gender_male"),
                        new ButtonData("Женский", "gender_female"))
        ));
    }

    public InlineKeyboardMarkup ageKeyboard() {
        return createKeyboard(
                List.of(AgeOption.values()).stream()
                        .map(opt -> List.of(new ButtonData(opt.getText(), opt.getCallback())))
                        .toList()
        );
    }

    public InlineKeyboardMarkup activityLevelKeyboard() {
        return createKeyboard(
                List.of(ActivityLevelOption.values()).stream()
                        .map(opt -> List.of(new ButtonData(opt.getText(), opt.getCallback())))
                        .toList()
        );
    }

    public InlineKeyboardMarkup goalKeyboard() {
        return createKeyboard(List.of(
                List.of(new ButtonData("Похудеть", "goal_lose")),
                List.of(new ButtonData("Поддерживать", "goal_maintain")),
                List.of(new ButtonData("Набрать", "goal_gain"))
        ));
    }

    public InlineKeyboardMarkup weightKeyboard() {
        return createKeyboard(List.of(
                List.of(
                        new ButtonData("−5", "weight_minus5"),
                        new ButtonData("+5", "weight_plus5"),
                        new ButtonData("Другая…", "weight_custom")
                )
        ));
    }

    public InlineKeyboardMarkup heightKeyboard() {
        return createKeyboard(List.of(
                List.of(
                        new ButtonData("−5", "height_minus5"),
                        new ButtonData("+5", "height_plus5"),
                        new ButtonData("Другая…", "height_custom")
                )
        ));
    }

    // Универсальный генератор клавиатур
    private InlineKeyboardMarkup createKeyboard(List<List<ButtonData>> buttonLayout) {
        List<List<InlineKeyboardButton>> keyboard = buttonLayout.stream()
                .map(row -> row.stream()
                        .map(btn -> InlineKeyboardButton.builder()
                                .text(btn.text())
                                .callbackData(btn.callbackData())
                                .build())
                        .toList())
                .toList();
        return InlineKeyboardMarkup.builder().keyboard(keyboard).build();
    }

    // Простая структура данных для кнопок
    private record ButtonData(String text, String callbackData) {}
}
