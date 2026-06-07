package io.vxd.textadventure.application.parser;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ParserTest {

    private Parser parser;

    @Before
    public void setUp() {
        parser = new Parser();
    }

    @Test
    public void testParseBlankInput() {
        Command cmd = parser.parse("");
        assertEquals(Verb.UNKNOWN, cmd.verb());
        assertEquals("", cmd.raw());
    }

    @Test
    public void testParseWhitespaceOnlyInput() {
        Command cmd = parser.parse("   ");
        assertEquals(Verb.UNKNOWN, cmd.verb());
    }

    @Test
    public void testParseSimpleLook() {
        Command cmd = parser.parse("look");
        assertEquals(Verb.LOOK, cmd.verb());
        assertFalse(cmd.directObject().isPresent());
    }

    @Test
    public void testParseLookSynonym() {
        Command cmd = parser.parse("l");
        assertEquals(Verb.LOOK, cmd.verb());
    }

    @Test
    public void testParseLookAt() {
        Command cmd = parser.parse("look at door");
        assertEquals(Verb.LOOK, cmd.verb());
        assertEquals("door", cmd.directObject().get());
    }

    @Test
    public void testParseExamine() {
        Command cmd = parser.parse("examine key");
        assertEquals(Verb.LOOK, cmd.verb());
        assertEquals("key", cmd.directObject().get());
    }

    @Test
    public void testParseSimpleTake() {
        Command cmd = parser.parse("take");
        assertEquals(Verb.TAKE, cmd.verb());
        assertFalse(cmd.directObject().isPresent());
    }

    @Test
    public void testParseTakeObject() {
        Command cmd = parser.parse("take key");
        assertEquals(Verb.TAKE, cmd.verb());
        assertEquals("key", cmd.directObject().get());
    }

    @Test
    public void testParsePickUpMultiWord() {
        Command cmd = parser.parse("pick up brass key");
        assertEquals(Verb.TAKE, cmd.verb());
        assertEquals("brass key", cmd.directObject().get());
    }

    @Test
    public void testParseGetObject() {
        Command cmd = parser.parse("get sword");
        assertEquals(Verb.TAKE, cmd.verb());
        assertEquals("sword", cmd.directObject().get());
    }

    @Test
    public void testParseGoWithDirection() {
        Command cmd = parser.parse("go north");
        assertEquals(Verb.GO, cmd.verb());
        assertEquals("north", cmd.directObject().get());
    }

    @Test
    public void testParseGoMove() {
        Command cmd = parser.parse("move east");
        assertEquals(Verb.GO, cmd.verb());
        assertEquals("east", cmd.directObject().get());
    }

    @Test
    public void testParseDirectionShortcutN() {
        Command cmd = parser.parse("n");
        assertEquals(Verb.GO, cmd.verb());
        assertEquals("north", cmd.directObject().get());
    }

    @Test
    public void testParseDirectionShortcutS() {
        Command cmd = parser.parse("s");
        assertEquals(Verb.GO, cmd.verb());
        assertEquals("south", cmd.directObject().get());
    }

    @Test
    public void testParseDirectionShortcutE() {
        Command cmd = parser.parse("e");
        assertEquals(Verb.GO, cmd.verb());
        assertEquals("east", cmd.directObject().get());
    }

    @Test
    public void testParseDirectionShortcutW() {
        Command cmd = parser.parse("w");
        assertEquals(Verb.GO, cmd.verb());
        assertEquals("west", cmd.directObject().get());
    }

    @Test
    public void testParseDirectionUp() {
        Command cmd = parser.parse("up");
        assertEquals(Verb.GO, cmd.verb());
        assertEquals("up", cmd.directObject().get());
    }

    @Test
    public void testParseDirectionDown() {
        Command cmd = parser.parse("down");
        assertEquals(Verb.GO, cmd.verb());
        assertEquals("down", cmd.directObject().get());
    }

    @Test
    public void testParseInventory() {
        Command cmd = parser.parse("inventory");
        assertEquals(Verb.INVENTORY, cmd.verb());
        assertFalse(cmd.directObject().isPresent());
    }

    @Test
    public void testParseInventorySynonym() {
        Command cmd = parser.parse("i");
        assertEquals(Verb.INVENTORY, cmd.verb());
    }

    @Test
    public void testParseUseObject() {
        Command cmd = parser.parse("use key");
        assertEquals(Verb.USE, cmd.verb());
        assertEquals("key", cmd.directObject().get());
    }

    @Test
    public void testParseUseXOnY() {
        Command cmd = parser.parse("use key on door");
        assertEquals(Verb.USE, cmd.verb());
        assertEquals("key", cmd.directObject().get());
        assertEquals("door", cmd.indirectObject().get());
    }

    @Test
    public void testParseTalkTo() {
        Command cmd = parser.parse("talk to guard");
        assertEquals(Verb.TALK, cmd.verb());
        assertEquals("guard", cmd.directObject().get());
    }

    @Test
    public void testParseTalk() {
        Command cmd = parser.parse("talk merchant");
        assertEquals(Verb.TALK, cmd.verb());
        assertEquals("merchant", cmd.directObject().get());
    }

    @Test
    public void testParsePutXInY() {
        Command cmd = parser.parse("put key in chest");
        assertEquals(Verb.PUT, cmd.verb());
        assertEquals("key", cmd.directObject().get());
        assertEquals("chest", cmd.indirectObject().get());
    }

    @Test
    public void testParsePutXOnY() {
        Command cmd = parser.parse("put ring on table");
        assertEquals(Verb.PUT, cmd.verb());
        assertEquals("ring", cmd.directObject().get());
        assertEquals("table", cmd.indirectObject().get());
    }

    @Test
    public void testParseSave() {
        Command cmd = parser.parse("save");
        assertEquals(Verb.SAVE, cmd.verb());
        assertFalse(cmd.directObject().isPresent());
    }

    @Test
    public void testParseLoad() {
        Command cmd = parser.parse("load");
        assertEquals(Verb.LOAD, cmd.verb());
        assertFalse(cmd.directObject().isPresent());
    }

    @Test
    public void testParseQuit() {
        Command cmd = parser.parse("quit");
        assertEquals(Verb.QUIT, cmd.verb());
    }

    @Test
    public void testParseExit() {
        Command cmd = parser.parse("exit");
        assertEquals(Verb.QUIT, cmd.verb());
    }

    @Test
    public void testParseUnknownVerb() {
        Command cmd = parser.parse("blah");
        assertEquals(Verb.UNKNOWN, cmd.verb());
        assertEquals("blah", cmd.raw());
    }

    @Test
    public void testParseUnknownVerbWithObject() {
        Command cmd = parser.parse("xyz something");
        assertEquals(Verb.UNKNOWN, cmd.verb());
        assertEquals("xyz something", cmd.raw());
    }

    @Test
    public void testParseMixedCase() {
        Command cmd = parser.parse("LOOK");
        assertEquals(Verb.LOOK, cmd.verb());
    }

    @Test
    public void testParseMixedCaseWithObject() {
        Command cmd = parser.parse("TAKE Key");
        assertEquals(Verb.TAKE, cmd.verb());
        assertEquals("Key", cmd.directObject().get());
    }

    @Test
    public void testParseNeverThrows() {
        // Should never throw, even with weird input
        parser.parse(null);
        parser.parse("!@#$%");
        parser.parse("123456");
        parser.parse("look at look at look at");
    }
}
