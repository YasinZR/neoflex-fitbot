package ru.neoflex.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static ru.neoflex.util.BotResponseUtils.sendTextWithKeyboard;

@Component
@RequiredArgsConstructor
public class NavigationButtonHandler {

    private final MainMenuHandler mainMenuHandler;

    public void handle(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();
        String userName = callbackQuery.getFrom().getFirstName();

        switch (data) {
            //case "NAV_BACK" -> sendTextWithKeyboard(chatId, "⬅️ Возврат назад (заглушка)", mainMenuHandler.createMainMenuMarkup());
            case "NAV_MENU" -> mainMenuHandler.showMenu(chatId);
        }
    }
}
