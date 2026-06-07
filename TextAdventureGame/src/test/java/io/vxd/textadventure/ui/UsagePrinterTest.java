package io.vxd.textadventure.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsagePrinterTest {

    @Test
    void getUsagePrintsHelpText() {
        String usage = UsagePrinter.getUsage();
        assertNotNull(usage);
        assertFalse(usage.isEmpty());
        assertTrue(usage.contains("--help"), "Should mention --help");
        assertTrue(usage.contains("--version"), "Should mention --version");
        assertTrue(usage.contains("--no-color"), "Should mention --no-color");
    }

    @Test
    void usageIncludesWorldPathDescription() {
        String usage = UsagePrinter.getUsage();
        assertTrue(usage.contains("world"), "Should describe world file argument");
    }
}
