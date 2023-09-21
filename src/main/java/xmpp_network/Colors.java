package xmpp_network;

public class Colors {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static String blackText(String text) {
        return ANSI_BLACK + text + ANSI_RESET;
    }

    public static String redText(String text) {
        return ANSI_RED + text + ANSI_RESET;
    }

    public static String greenText(String text) {
        return ANSI_GREEN + text + ANSI_RESET;
    }

    public static String yellowText(String text) {
        return ANSI_YELLOW + text + ANSI_RESET;
    }

    public static String blueText(String text) {
        return ANSI_BLUE + text + ANSI_RESET;
    }

    public static String purpleText(String text) {
        return ANSI_PURPLE + text + ANSI_RESET;
    }

    public static String cyanText(String text) {
        return ANSI_CYAN + text + ANSI_RESET;
    }

    public static String whiteText(String text) {
        return ANSI_WHITE + text + ANSI_RESET;
    }
}
