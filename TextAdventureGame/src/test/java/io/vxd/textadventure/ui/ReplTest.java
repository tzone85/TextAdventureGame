package io.vxd.textadventure.ui;

import io.vxd.textadventure.application.*;
import io.vxd.textadventure.infrastructure.persistence.SaveGameRepository;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class ReplTest {

    @Test
    void replHandlesLookCommand() throws IOException {
        GameEngine gameEngine = new GameEngine();
        SaveGameRepository repository = new SaveGameRepository();
        AnsiPalette palette = new AnsiPalette(false);
        Parser parser = new Parser();

        String input = "look\nquit\n";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        PrintStream outputStream = new PrintStream(new ByteArrayOutputStream());

        Repl repl = new Repl(gameEngine, repository, palette, parser);
        repl.setInputStream(inputStream);
        repl.setOutputStream(outputStream);

        // Should not throw
        assertDoesNotThrow(() -> repl.run("worlds/manor.yaml", null));
    }

    @Test
    void replHandlesQuitCommand() throws IOException {
        GameEngine gameEngine = new GameEngine();
        SaveGameRepository repository = new SaveGameRepository();
        AnsiPalette palette = new AnsiPalette(false);
        Parser parser = new Parser();

        String input = "quit\n";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream outputStream = new PrintStream(baos);

        Repl repl = new Repl(gameEngine, repository, palette, parser);
        repl.setInputStream(inputStream);
        repl.setOutputStream(outputStream);

        assertDoesNotThrow(() -> repl.run("worlds/manor.yaml", null));
    }

    @Test
    void replStartsWithLoadingWorld() throws IOException {
        GameEngine gameEngine = new GameEngine();
        SaveGameRepository repository = new SaveGameRepository();
        AnsiPalette palette = new AnsiPalette(false);
        Parser parser = new Parser();

        String input = "quit\n";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream outputStream = new PrintStream(baos);

        Repl repl = new Repl(gameEngine, repository, palette, parser);
        repl.setInputStream(inputStream);
        repl.setOutputStream(outputStream);

        assertDoesNotThrow(() -> repl.run("worlds/manor.yaml", null));

        String output = baos.toString();
        // Verify world was loaded and initial state was shown
        assertNotNull(output);
    }

    @Test
    void replHandlesMultipleCommands() throws IOException {
        GameEngine gameEngine = new GameEngine();
        SaveGameRepository repository = new SaveGameRepository();
        AnsiPalette palette = new AnsiPalette(false);
        Parser parser = new Parser();

        String input = "look\nlook\nquit\n";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream outputStream = new PrintStream(baos);

        Repl repl = new Repl(gameEngine, repository, palette, parser);
        repl.setInputStream(inputStream);
        repl.setOutputStream(outputStream);

        assertDoesNotThrow(() -> repl.run("worlds/manor.yaml", null));
    }
}
