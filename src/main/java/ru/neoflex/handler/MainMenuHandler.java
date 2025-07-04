package ru.neoflex.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class MainMenuHandler {

    public SendMessage createMainMenu(long chatId, String userName) {
        String text = "Привет, " + userName + "! Я твой персональный помощник по здоровью и спорту.\n" +
                "Выбери, что тебя интересует:";

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(List.of(InlineKeyboardButton.builder().text("💪 Привычки").callbackData("habits").build()));
        rows.add(List.of(InlineKeyboardButton.builder().text("🧮 БЖУ").callbackData("bju").build()));
        rows.add(List.of(InlineKeyboardButton.builder().text("💧 Вода").callbackData("water").build()));
        rows.add(List.of(InlineKeyboardButton.builder().text("📊 Статистика").callbackData("stats").build()));
        rows.add(List.of(InlineKeyboardButton.builder().text("⚙️ Настройки").callbackData("settings").build()));

        markup.setKeyboard(rows);

        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .replyMarkup(markup)
                .build();
    }
}
