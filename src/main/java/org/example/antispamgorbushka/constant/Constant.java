package org.example.antispamgorbushka.constant;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Constant {
    @NoArgsConstructor(access = PRIVATE)
    public final class App {

    }

    @NoArgsConstructor(access = PRIVATE)
    public final class Command {

        public static final String COMMAND_START = "/start";

    }

    public static String APP_NAME = "AntiSpamGorbushka";
    public static String USER_DIR = "user.dir";

}
