package org.example.antispamgorbushka.service.menu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
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
    @Autowired
    private ObjectMapper objectMapper;

    private List<String> expectedWords = List.of("куплю", "предложите");
    private List<String> blockWords = List.of("http", "https", "@", "чат", "канал", "группа", "переходи"
            , "приходи", "профиль", "бот");

    public List<PartialBotApiMethod> messageProcess(Update update) {
        if (update.hasEditedMessage()) {
            try {
                log.info("[Check object 1]: {}", objectMapper.writeValueAsString(update.getEditedMessage()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return checkMessage(update.getEditedMessage());
        }
        if (update.hasMessage()) {
            try {
                log.info("[Check object 2]: {}", objectMapper.writeValueAsString(update.getMessage()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
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

    private boolean isChannelOwnerChat(Long chatId) {
        if (chatId == null) {
            return false;
        }
        return botConfig.getOwnerUserChatId().contains(chatId);
    }

    private boolean isChannelOwnerChat(String senderUsername) {
        if (senderUsername == null) {
            return false;
        }
        return botConfig.getOwnerUsers().contains(senderUsername);
    }

    private List<PartialBotApiMethod> checkMessage(Message message) {
        var text = message.getText();
        var chatId = message.getChatId();
        try {
            if (message.getSenderChat() != null) {
                val senderUserName = message.getSenderChat().getUserName();
                log.info("senderUserName [check owner], chatId: [{}], text: [{}], senderUserName: [{}]", chatId, text, senderUserName);
                if (isChannelOwnerChat(senderUserName) || (text != null && text.equals(COMMAND_START))) {
                    log.info("The message ok because Sender [username is owner], chatId: [{}], text: [{}], senderUserName: [{}]", chatId, text, senderUserName);
                    return prepareEmptyAnswer();
                }
            }
            if (message.getFrom() != null) {
                var senderUserName = message.getFrom().getUserName();
                var senderId = message.getFrom().getId();
                if (isChannelOwnerChat(senderId)) {
                    log.info("The message ok because Sender [chatId is owner], chatId: [{}], text: [{}], senderUserName: [{}]", chatId, text);
                    return prepareEmptyAnswer();
                }
                if (isChannelOwnerChat(senderUserName) || (StringUtils.isNotEmpty(text) && text.equals(COMMAND_START))) {
                    log.info("The message ok because Sender [isChannelOwnerChat or start], chatId: [{}], text: [{}], senderUserName: [{}]", chatId, text, senderUserName);
                    return prepareEmptyAnswer();
                }
            } else {
                log.error("Странный кейс:" + message);
            }
            if (!hasExpectedWord(text)) {
                log.info("The message will be deleted because [hasNotExpectedWord], chatId: [{}], text: [{}]", chatId, text);
                return prepareDeleteAnswer(chatId, message.getMessageId());
            }
            if (hasBlockWord(text)) {
                log.info("The message will be deleted because [hasBlockWord], chatId: [{}], text: [{}]", chatId, text);
                return prepareDeleteAnswer(chatId, message.getMessageId());
            }
            log.info("The message ok because [finish all checks], chatId: [{}], text: [{}]", chatId, text);
            return prepareEmptyAnswer();
        } catch (Exception ex) {
            log.error("The message will be deleted because [random error], text: [{}]", text, ex);
            return prepareDeleteAnswer(chatId, message.getMessageId());
        }
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