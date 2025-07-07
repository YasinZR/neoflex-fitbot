package ru.neoflex.util;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.bots.AbsSender;

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
}
