package org.example.antispamgorbushka.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.antispamgorbushka.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.example.antispamgorbushka.service.menu.MenuService;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private BotConfig botConfig;

    @Autowired
    private MenuService menuService;

    @PostConstruct
    public void init() {
        log.info("==" + "Server was starded. Version: " + botConfig.getBotVersion() + "==================================================================================================");
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotUserName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        val answers = menuService.messageProcess(update);
        for (val answer : answers) {
            try {
                if (answer instanceof BotApiMethod) {
                    execute((BotApiMethod) answer);
                }
                if (answer instanceof SendDocument) {
                    execute((SendDocument) answer);
                }
                if (answer instanceof SendPhoto) {
                    execute((SendPhoto) answer);
                }
            } catch (TelegramApiException e) {
                log.error("Ошибка во время обработки сообщения: " + e.getMessage());
            }
        }
    }

}
