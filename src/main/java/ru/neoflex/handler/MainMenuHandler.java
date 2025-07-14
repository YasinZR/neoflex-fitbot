package ru.neoflex.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static ru.neoflex.util.BotResponseUtils.sendTextWithKeyboard;

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

    public InlineKeyboardMarkup createMainMenuMarkup() {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(
                                InlineKeyboardButton.builder().text("🏋️‍♂️ Тренировки").callbackData("MENU_WORKOUT").build()
                        ),
                        List.of(
                                InlineKeyboardButton.builder().text("🍽 Питание").callbackData("MENU_NUTRITION").build()
                        ),
                        List.of(
                                InlineKeyboardButton.builder().text("💧 Вода").callbackData("MENU_WATER").build()
                        ),
                        List.of(
                                InlineKeyboardButton.builder().text("🧮 Калькулятор").callbackData("MENU_CALCULATOR").build()
                        ),
                        List.of(
                                InlineKeyboardButton.builder().text("📊 Статистика").callbackData("MENU_STATS").build()
                        ),
                        List.of(
                                InlineKeyboardButton.builder().text("👤 Профиль").callbackData("MENU_PROFILE").build()
                        ),
                        List.of(
                                InlineKeyboardButton.builder().text("❓ Помощь").callbackData("MENU_HELP").build()
                        )
                ))
                .build();
    }



    public void showMenu(long chatId) {
        sendTextWithKeyboard(
                chatId,
                "🏠 Главное меню:\nВыбери, что тебя интересует:",
                createMainMenuMarkup()
        );
    }

}
