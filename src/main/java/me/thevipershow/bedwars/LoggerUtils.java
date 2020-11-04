package me.thevipershow.bedwars;

import java.util.logging.Logger;

public final class LoggerUtils {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void logColor(final Logger logger, final String text) {
        logger.info(text.replace("&r", ANSI_RESET)
                        .replace("&0", ANSI_BLACK)
                        .replace("&c", ANSI_RED)
                        .replace("&a", ANSI_GREEN)
                        .replace("&e", ANSI_YELLOW)
                        .replace("&9", ANSI_BLUE)
                        .replace("&5", ANSI_PURPLE)
                        .replace("&3", ANSI_CYAN)
                        .replace("&f", ANSI_WHITE)
                        .concat(ANSI_RESET));
    }

}
