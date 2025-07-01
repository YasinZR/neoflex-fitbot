package ru.neoflex;

import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.neoflex.config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class NeoflexFitBot  extends TelegramLongPollingBot {
    private final BotConfig config;

    public NeoflexFitBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom().getFirstName();

            if (messageText.equals("/start")) {
                sendMainMenu(chatId, userName);
//                String reply = "Привет, " + userName + "! 👋\nЯ твой персональный помощник по здоровью и спорту.\n" +
//                        "Вместе мы сделаем тебя сильнее, выносливее и организованнее!";
//                sendMessage(chatId, reply);
            }
        }
    }

    private void sendMainMenu(long chatId, String userName) {
        String text = "Привет, " + userName + "! Я твой персональный помощник по здоровью и спорту. \n " +
                "Вместе мы сделаем тебя сильнее, выносливее и организованнее!\nВыбери, что тебя интересует:";

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(List.of(InlineKeyboardButton.builder().text("💪 Привычки").callbackData("habits").build()));
        rows.add(List.of(InlineKeyboardButton.builder().text("🧮 БЖУ").callbackData("bju").build()));
        rows.add(List.of(InlineKeyboardButton.builder().text("💧 Вода").callbackData("water").build()));
        rows.add(List.of(InlineKeyboardButton.builder().text("📊 Статистика").callbackData("stats").build()));
        rows.add(List.of(InlineKeyboardButton.builder().text("⚙️ Настройки").callbackData("settings").build()));

        markup.setKeyboard(rows);

        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .replyMarkup(markup)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage(String.valueOf(chatId), text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}