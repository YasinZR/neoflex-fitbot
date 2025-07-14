package ru.neoflex.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class NavigationMarkupFactory {

    public static InlineKeyboardMarkup backAndMenu() {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(
                                InlineKeyboardButton.builder().text("🔙 Назад").callbackData("NAV_BACK").build(),
                                InlineKeyboardButton.builder().text("🏠 Главное меню").callbackData("NAV_MENU").build()
                        )
                ))
                .build();
    }

    public static InlineKeyboardMarkup navigationMarkup() {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(
                                //InlineKeyboardButton.builder().text("🔙 Назад").callbackData("NAV_BACK").build(),
                                InlineKeyboardButton.builder().text("🏠 Главное меню").callbackData("NAV_MENU").build()
                        )
                ))
                .build();
    }

}
