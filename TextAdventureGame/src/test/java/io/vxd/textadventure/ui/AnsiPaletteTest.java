package io.vxd.textadventure.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnsiPaletteTest {

    @Test
    void roomTitleWhenColorEnabledEmitsCode() {
        AnsiPalette palette = new AnsiPalette(true);
        String result = palette.roomTitle("Manor");
        assertTrue(result.contains("["), "Should contain ANSI escape code");
        assertTrue(result.contains("Manor"), "Should contain the title");
    }

    @Test
    void roomTitleWhenColorDisabledReturnsEmpty() {
        AnsiPalette palette = new AnsiPalette(false);
        String result = palette.roomTitle("Manor");
        assertEquals("", result);
    }

    @Test
    void itemWhenColorEnabledEmitsCode() {
        AnsiPalette palette = new AnsiPalette(true);
        String result = palette.item("sword");
        assertTrue(result.contains("["), "Should contain ANSI escape code");
        assertTrue(result.contains("sword"), "Should contain the item");
    }

    @Test
    void itemWhenColorDisabledReturnsEmpty() {
        AnsiPalette palette = new AnsiPalette(false);
        String result = palette.item("sword");
        assertEquals("", result);
    }

    @Test
    void npcWhenColorEnabledEmitsCode() {
        AnsiPalette palette = new AnsiPalette(true);
        String result = palette.npc("wizard");
        assertTrue(result.contains("["), "Should contain ANSI escape code");
        assertTrue(result.contains("wizard"), "Should contain the NPC");
    }

    @Test
    void npcWhenColorDisabledReturnsEmpty() {
        AnsiPalette palette = new AnsiPalette(false);
        String result = palette.npc("wizard");
        assertEquals("", result);
    }

    @Test
    void errorWhenColorEnabledEmitsCode() {
        AnsiPalette palette = new AnsiPalette(true);
        String result = palette.error("Invalid command");
        assertTrue(result.contains("["), "Should contain ANSI escape code");
        assertTrue(result.contains("Invalid command"), "Should contain the error message");
    }

    @Test
    void errorWhenColorDisabledReturnsEmpty() {
        AnsiPalette palette = new AnsiPalette(false);
        String result = palette.error("Invalid command");
        assertEquals("", result);
    }

    @Test
    void resetReturnsResetCodeWhenEnabled() {
        AnsiPalette palette = new AnsiPalette(true);
        String result = palette.reset();
        assertTrue(result.contains("["), "Should contain ANSI reset code");
    }

    @Test
    void resetReturnsEmptyWhenDisabled() {
        AnsiPalette palette = new AnsiPalette(false);
        String result = palette.reset();
        assertEquals("", result);
    }
}
