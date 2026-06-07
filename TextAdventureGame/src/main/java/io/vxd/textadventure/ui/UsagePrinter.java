package io.vxd.textadventure.ui;

public class UsagePrinter {

    public static String getUsage() {
        return "Usage: java -jar textadventure.jar <world-file> [options]\n" +
               "\n" +
               "Arguments:\n" +
               "  world-file              Path to the world YAML file\n" +
               "\n" +
               "Options:\n" +
               "  --help                  Show this help message and exit\n" +
               "  --version               Show version and exit\n" +
               "  --no-color              Disable ANSI color output\n" +
               "  --load=<path>           Load a saved game from the specified path\n";
    }
}
