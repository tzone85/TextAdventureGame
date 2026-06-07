package io.vxd.textadventure.ui;

import io.vxd.textadventure.application.GameEngine;
import io.vxd.textadventure.infrastructure.persistence.IncompatibleSaveVersionException;
import io.vxd.textadventure.infrastructure.persistence.SaveGameRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        if (isHelpRequested(args)) {
            System.out.println(UsagePrinter.getUsage());
            System.exit(0);
        }

        if (isVersionRequested(args)) {
            String version = getVersion();
            System.out.println(version);
            System.exit(0);
        }

        String worldPath = getWorldPath(args);
        if (worldPath == null) {
            System.err.println("Error: world file path is required");
            System.err.println(UsagePrinter.getUsage());
            System.exit(1);
        }

        boolean colorEnabled = !isColorDisabled(args);
        String loadPath = extractLoadPath(args);

        GameEngine gameEngine = new GameEngine();
        SaveGameRepository saveGameRepository = new SaveGameRepository();
        AnsiPalette palette = new AnsiPalette(colorEnabled);
        Parser parser = new Parser();

        try {
            Repl repl = new Repl(gameEngine, saveGameRepository, palette, parser);
            repl.run(worldPath, loadPath);
        } catch (IOException | IncompatibleSaveVersionException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    static boolean isHelpRequested(String[] args) {
        for (String arg : args) {
            if ("--help".equals(arg)) {
                return true;
            }
        }
        return false;
    }

    static boolean isVersionRequested(String[] args) {
        for (String arg : args) {
            if ("--version".equals(arg)) {
                return true;
            }
        }
        return false;
    }

    static boolean isColorDisabled(String[] args) {
        for (String arg : args) {
            if ("--no-color".equals(arg)) {
                return true;
            }
        }
        return false;
    }

    static String extractLoadPath(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("--load=")) {
                return arg.substring("--load=".length());
            }
        }
        return null;
    }

    static String getWorldPath(String[] args) {
        for (String arg : args) {
            if (!arg.startsWith("--")) {
                return arg;
            }
        }
        return null;
    }

    private static String getVersion() {
        try {
            Properties props = new Properties();
            props.load(Main.class.getResourceAsStream("/version.properties"));
            return props.getProperty("implementation.version", "unknown");
        } catch (Exception e) {
            return "unknown";
        }
    }
}
