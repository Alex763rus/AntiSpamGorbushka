package org.example.antispamgorbushka.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import static org.example.antispamgorbushka.constant.Constant.USER_DIR;
import static org.example.tgcommons.constant.Constant.TextConstants.SHIELD;

@Configuration
@Data
@PropertySource("application.properties")
public class BotConfig {

    @Value("${bot.version}")
    String botVersion;

    @Value("${bot.username}")
    String botUserName;

    @Value("${bot.token}")
    String botToken;

    @Value("${admin.chatid}")
    String adminChatId;

    @Value("${user.owner.chatId}")
    Long userOwnerChatId;

    @Value("${channel.owner.chatId}")
    Long channelOwnerChatId;

    private String getCurrentPath() {
        return System.getProperty(USER_DIR) + SHIELD;
    }


}
