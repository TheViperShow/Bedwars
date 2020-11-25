package me.thevipershow.bedwars;

import java.util.logging.Logger;

public final class LoggerUtils {

    private enum ANSI {
        RESET("\u001B[0m", 'r'),
        BLACK("\u001B[30m", '0'),
        RED("\u001B[31m", 'c'),
        GREEN("\u001B[32m", 'a'),
        YELLOW("\u001B[33m", 'e'),
        BLUE("\u001B[34m", '9'),
        PURPLE("\u001B[35m", '5'),
        CYAN("\u001B[36m", '3'),
        WHITE("\u001B[37m", 'f');

        private final String placeholder;
        private final char colorCode;

        ANSI(String placeholder, char colorCode) {
            this.placeholder = placeholder;
            this.colorCode = colorCode;
        }

        public final String getPlaceholder() {
            return placeholder;
        }

        public final char getColorCode() {
            return colorCode;
        }
    }

    public static void logColor(final Logger logger, String text) {
        for (ANSI ansi : ANSI.values()) {
            text = text.replace("&" + Character.toString(ansi.colorCode), ansi.placeholder);
        }
        logger.info(text + ANSI.RESET.placeholder);
    }

}
