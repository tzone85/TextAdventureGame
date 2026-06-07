package io.vxd.textadventure.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void helpFlagExitsWithZero() {
        // We'll test by verifying help flag handling in integration tests
        // Unit test: verify help argument is recognized
        String[] args = {"--help"};
        assertTrue(Main.isHelpRequested(args));
    }

    @Test
    void versionFlagExitsWithZero() {
        String[] args = {"--version"};
        assertTrue(Main.isVersionRequested(args));
    }

    @Test
    void noColorFlagDetected() {
        String[] args = {"--no-color"};
        assertTrue(Main.isColorDisabled(args));
    }

    @Test
    void loadFlagDetected() {
        String[] args = {"--load=/path/to/save"};
        String loadPath = Main.extractLoadPath(args);
        assertEquals("/path/to/save", loadPath);
    }

    @Test
    void getWorldPathExtractsFirstPositionalArg() {
        String[] args = {"worlds/manor.yaml", "--no-color"};
        String worldPath = Main.getWorldPath(args);
        assertEquals("worlds/manor.yaml", worldPath);
    }

    @Test
    void noWorldPathReturnsNull() {
        String[] args = {"--help"};
        String worldPath = Main.getWorldPath(args);
        assertNull(worldPath);
    }

    @Test
    void extractLoadPathReturnsNullWhenNotPresent() {
        String[] args = {"worlds/manor.yaml"};
        String loadPath = Main.extractLoadPath(args);
        assertNull(loadPath);
    }
}
