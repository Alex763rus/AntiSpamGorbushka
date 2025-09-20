package org.example.antispamgorbushka.service.menu;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.antispamgorbushka.config.BotConfig;
import org.example.tgcommons.model.wrapper.DeleteMessageWrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.example.antispamgorbushka.constant.Constant.Command.COMMAND_START;

@Slf4j
@Service
public class MenuService {

    @Autowired
    private BotConfig botConfig;

    private List<String> expectedWords = List.of("куплю", "предложите");
    private List<String> blockWords = List.of("http", "https", "@", "чат", "канал", "группа", "переходи"
            , "приходи", "профиль", "бот");

    public List<PartialBotApiMethod> messageProcess(Update update) {
        if (update.hasEditedMessage()) {
            return checkMessage(update.getEditedMessage());
        }
        if (update.hasMessage()) {
            return checkMessage(update.getMessage());
        }
        return prepareEmptyAnswer();
    }

    private boolean wordsExists(String message, List<String> words) {
        if (message == null) {
            return false;
        }
        val messageLowerCase = message.toLowerCase();
        val wordsExists = words.stream()
                .filter(word -> messageLowerCase.contains(word))
                .findFirst()
                .orElse(null);
        return wordsExists != null;
    }

    private boolean hasBlockWord(String message) {
        return wordsExists(message, blockWords);
    }

    private boolean hasExpectedWord(String message) {
        return wordsExists(message, expectedWords);
    }

    private boolean isChannelOwnerChat(String senderUsername) {
        if (senderUsername == null) {
            return false;
        }
        return botConfig.getOwnerUsers().contains(senderUsername);
    }

    private List<PartialBotApiMethod> checkMessage(Message message) {
        try {
            val text = message.getText();
            if (message.getSenderChat() != null) {
                val senderUserName = message.getSenderChat().getUserName();
                if (isChannelOwnerChat(senderUserName) || (text != null && text.equals(COMMAND_START))) {
                    return prepareEmptyAnswer();
                }
            }
            if (message.getFrom() != null) {
                val fromUserName = message.getFrom().getUserName();
                if (isChannelOwnerChat(fromUserName) || (text != null && text.equals(COMMAND_START))) {
                    return prepareEmptyAnswer();
                }
            } else {
                log.error("Странный кейс:" + message);
            }
            if (!hasExpectedWord(text) || hasBlockWord(text)) {
                //log.info("Удаляем сообщение, причина: !hasExpectedWord(text) || hasBlockWord(text)" + message);
                return prepareDeleteAnswer(message.getChatId(), message.getMessageId());
            }
        } catch (Exception ex) {
            //log.error("Удаляем сообщение, из-за ошибки:" + ex + NEW_LINE + "Сообщение:" + message);
            return prepareDeleteAnswer(message.getChatId(), message.getMessageId());
        }
        return prepareEmptyAnswer();
    }

    private List<PartialBotApiMethod> prepareEmptyAnswer() {
        return List.of();
    }

    private List<PartialBotApiMethod> prepareDeleteAnswer(final Long chatId, final Integer messageId) {
        return DeleteMessageWrap.init()
                .setChatIdLong(chatId)
                .setMessageId(messageId)
                .build().createMessageList();
    }
}