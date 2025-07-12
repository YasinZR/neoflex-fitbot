package ru.neoflex.util;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class BotResponseUtils {

    private static AbsSender sender;

    public static void init(AbsSender bot) {
        sender = bot;
    }

    public static void executeSafe(SendMessage message) {
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения", e);
        }
    }

    public static void sendText(long chatId, String text) {
        executeSafe(SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .build());
    }

    public static void sendTextWithKeyboard(long chatId, String text, InlineKeyboardMarkup markup) {
        executeSafe(SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .replyMarkup(markup)
                .build());
    }

    public static void sendButtons(Long chatId, String text, Map<String, String> buttons) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Map.Entry<String, String> entry : buttons.entrySet()) {
            InlineKeyboardButton button = new InlineKeyboardButton(entry.getKey());
            button.setCallbackData(entry.getValue());
            rows.add(List.of(button));
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(markup);
        message.setParseMode("Markdown");

        try {
            sender.execute(message); // ✅ правильно
        } catch (TelegramApiException e) {
            log.error("Failed to send buttons", e);
        }

    }


}
