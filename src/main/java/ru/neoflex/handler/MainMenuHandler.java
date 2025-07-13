package ru.neoflex.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class MainMenuHandler {

    public SendMessage createMainMenu(long chatId, String name) {
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(
                                InlineKeyboardButton.builder().text("🏋️‍♂️ Тренировки").callbackData("workout_menu").build()
                        ),
                        List.of(
                                InlineKeyboardButton.builder().text("🍽 Питание").callbackData("nutrition_menu").build()
                        ),
                        List.of(
                                InlineKeyboardButton.builder().text("💧 Вода").callbackData("WATER_MENU").build()
                        )
                ))
                .build();

        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(name + "! Я твой персональный помощник по здоровью и спорту.\n" +
                        "Выбери, что тебя интересует:")
                .replyMarkup(markup)
                .build();
    }

}
