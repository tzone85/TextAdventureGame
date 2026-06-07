package io.vxd.textadventure.ui;

public class AnsiPalette {
    private static final String RESET = "[0m";
    private static final String BOLD_CYAN = "[1;36m";
    private static final String YELLOW = "[33m";
    private static final String MAGENTA = "[35m";
    private static final String RED = "[31m";

    private final boolean colorEnabled;

    public AnsiPalette(boolean colorEnabled) {
        this.colorEnabled = colorEnabled;
    }

    public String roomTitle(String title) {
        if (!colorEnabled) return "";
        return BOLD_CYAN + title + RESET;
    }

    public String item(String itemName) {
        if (!colorEnabled) return "";
        return YELLOW + itemName + RESET;
    }

    public String npc(String npcName) {
        if (!colorEnabled) return "";
        return MAGENTA + npcName + RESET;
    }

    public String error(String message) {
        if (!colorEnabled) return "";
        return RED + message + RESET;
    }

    public String reset() {
        if (!colorEnabled) return "";
        return RESET;
    }
}
