package io.vxd.textadventure.ui;

import io.vxd.textadventure.application.Command;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    private final Parser parser = new Parser();

    @Test
    void parseLookCommand() {
        Command cmd = parser.parse("look");
        assertEquals("LOOK", cmd.action());
        assertNull(cmd.target());
        assertNull(cmd.container());
    }

    @Test
    void parseInventoryCommand() {
        Command cmd = parser.parse("inventory");
        assertEquals("INVENTORY", cmd.action());
    }

    @Test
    void parseTakeCommandWithTarget() {
        Command cmd = parser.parse("take sword");
        assertEquals("TAKE", cmd.action());
        assertEquals("sword", cmd.target());
    }

    @Test
    void parseGoCommandWithTarget() {
        Command cmd = parser.parse("go north");
        assertEquals("GO", cmd.action());
        assertEquals("north", cmd.target());
    }

    @Test
    void parseUseCommandWithTarget() {
        Command cmd = parser.parse("use torch");
        assertEquals("USE", cmd.action());
        assertEquals("torch", cmd.target());
    }

    @Test
    void parseTalkCommandWithTarget() {
        Command cmd = parser.parse("talk wizard");
        assertEquals("TALK", cmd.action());
        assertEquals("wizard", cmd.target());
    }

    @Test
    void parsePutCommandWithTargetAndContainer() {
        Command cmd = parser.parse("put sword in chest");
        assertEquals("PUT", cmd.action());
        assertEquals("sword", cmd.target());
        assertEquals("chest", cmd.container());
    }

    @Test
    void parseSaveCommand() {
        Command cmd = parser.parse("save");
        assertEquals("SAVE", cmd.action());
    }

    @Test
    void parseLoadCommand() {
        Command cmd = parser.parse("load");
        assertEquals("LOAD", cmd.action());
    }

    @Test
    void parseQuitCommand() {
        Command cmd = parser.parse("quit");
        assertEquals("QUIT", cmd.action());
    }

    @Test
    void parseHandlesMultipleWordsInTarget() {
        Command cmd = parser.parse("take red potion");
        assertEquals("TAKE", cmd.action());
        assertEquals("red potion", cmd.target());
    }

    @Test
    void parseHandlesMultipleWordsWithInPreposition() {
        Command cmd = parser.parse("put red potion in wooden chest");
        assertEquals("PUT", cmd.action());
        assertEquals("red potion", cmd.target());
        assertEquals("wooden chest", cmd.container());
    }

    @Test
    void parseEmptyStringReturnsLookCommand() {
        Command cmd = parser.parse("");
        assertEquals("LOOK", cmd.action());
    }

    @Test
    void parseWhitespaceReturnsLookCommand() {
        Command cmd = parser.parse("   ");
        assertEquals("LOOK", cmd.action());
    }

    @Test
    void parseCaseInsensitive() {
        Command cmd = parser.parse("LOOK");
        assertEquals("LOOK", cmd.action());

        cmd = parser.parse("Look");
        assertEquals("LOOK", cmd.action());
    }
}
