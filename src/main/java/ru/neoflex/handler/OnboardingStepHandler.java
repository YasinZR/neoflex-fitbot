package ru.neoflex.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class OnboardingStepHandler {

    public InlineKeyboardMarkup genderKeyboard() {
        return InlineKeyboardMarkup.builder().keyboard(List.of(
                List.of(
                        InlineKeyboardButton.builder().text("Мужской").callbackData("gender_male").build(),
                        InlineKeyboardButton.builder().text("Женский").callbackData("gender_female").build()
                )
        )).build();
    }

    public InlineKeyboardMarkup ageKeyboard() {
        return InlineKeyboardMarkup.builder().keyboard(List.of(
                List.of(InlineKeyboardButton.builder().text("18–25").callbackData("age_18").build()),
                List.of(InlineKeyboardButton.builder().text("26–35").callbackData("age_26").build()),
                List.of(InlineKeyboardButton.builder().text("36–45").callbackData("age_36").build()),
                List.of(InlineKeyboardButton.builder().text("46+").callbackData("age_46").build())
        )).build();
    }

    public InlineKeyboardMarkup activityLevelKeyboard() {
        return InlineKeyboardMarkup.builder().keyboard(List.of(
                List.of(InlineKeyboardButton.builder().text("Низкий").callbackData("activity_low").build()),
                List.of(InlineKeyboardButton.builder().text("Средний").callbackData("activity_medium").build()),
                List.of(InlineKeyboardButton.builder().text("Высокий").callbackData("activity_high").build()),
                List.of(InlineKeyboardButton.builder().text("🏃 Профи").callbackData("activity_pro").build())
        )).build();
    }

    public InlineKeyboardMarkup goalKeyboard() {
        return InlineKeyboardMarkup.builder().keyboard(List.of(
                List.of(InlineKeyboardButton.builder().text("Похудеть").callbackData("goal_lose").build()),
                List.of(InlineKeyboardButton.builder().text("Поддерживать").callbackData("goal_maintain").build()),
                List.of(InlineKeyboardButton.builder().text("Набрать").callbackData("goal_gain").build())
        )).build();
    }
    public InlineKeyboardMarkup weightKeyboard() {
        return InlineKeyboardMarkup.builder().keyboard(List.of(
                List.of(
                        InlineKeyboardButton.builder().text("−5").callbackData("weight_minus5").build(),
                        InlineKeyboardButton.builder().text("+5").callbackData("weight_plus5").build(),
                        InlineKeyboardButton.builder().text("Другая…").callbackData("weight_custom").build()
                )
        )).build();
    }

    public InlineKeyboardMarkup heightKeyboard() {
        return InlineKeyboardMarkup.builder().keyboard(List.of(
                List.of(
                        InlineKeyboardButton.builder().text("−5").callbackData("height_minus5").build(),
                        InlineKeyboardButton.builder().text("+5").callbackData("height_plus5").build(),
                        InlineKeyboardButton.builder().text("Другая…").callbackData("height_custom").build()
                )
        )).build();
    }

}
