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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.antispamgorbushka.constant.Constant.Command.COMMAND_START;

@Slf4j
@Service
public class MenuService {

    @Autowired
    private BotConfig botConfig;

    private List<String> expectedWords = List.of("куплю", "продам", "предложите");
    private List<String> blockWords = List.of("http", "https", "@");

    private boolean wordsExists(String message, List<String> words) {
        val wordsExists = words.stream()
                .filter(word -> message.toLowerCase().contains(word))
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
        return chatId.equals(botConfig.getChannelOwnerChatId()) || chatId.equals(botConfig.getUserOwnerChatId());
    }

    public List<PartialBotApiMethod> messageProcess(Update update) {
        if (update.hasEditedMessage()) {
            return checkMessage(update.getEditedMessage());
        }
        if (update.hasMessage()) {
            return checkMessage(update.getMessage());
        }
        return prepareEmptyAnswer();
    }

    private List<PartialBotApiMethod> checkMessage(Message message) {
        val text = message.getText();
        if(text == null){
            return prepareDeleteAnswer(message.getChatId(), message.getMessageId());
        }
        val userId = message.getFrom().getId();
        if (isChannelOwnerChat(userId) || text.equals(COMMAND_START)) {
            return prepareEmptyAnswer();
        }
        if (!hasExpectedWord(text) || hasBlockWord(text)) {
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

    private String getChatId(Update update) {
        if (update.hasMessage()) {
            return String.valueOf(update.getMessage().getChatId());
        }
        if (update.hasCallbackQuery()) {
            return String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        }
        return null;
    }


}