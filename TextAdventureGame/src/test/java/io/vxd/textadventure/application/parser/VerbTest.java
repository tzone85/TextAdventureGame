package io.vxd.textadventure.application.parser;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Optional;

public class VerbTest {

    @Test
    public void testVerbEnumExists() {
        assertNotNull(Verb.LOOK);
        assertNotNull(Verb.TAKE);
        assertNotNull(Verb.GO);
        assertNotNull(Verb.UNKNOWN);
    }

    @Test
    public void testLookSynonyms() {
        assertEquals(Verb.LOOK, Verb.fromString("look"));
        assertEquals(Verb.LOOK, Verb.fromString("l"));
        assertEquals(Verb.LOOK, Verb.fromString("examine"));
    }

    @Test
    public void testTakeSynonyms() {
        assertEquals(Verb.TAKE, Verb.fromString("take"));
        assertEquals(Verb.TAKE, Verb.fromString("get"));
    }

    @Test
    public void testGoSynonyms() {
        assertEquals(Verb.GO, Verb.fromString("go"));
        assertEquals(Verb.GO, Verb.fromString("move"));
        assertEquals(Verb.GO, Verb.fromString("n"));
        assertEquals(Verb.GO, Verb.fromString("north"));
        assertEquals(Verb.GO, Verb.fromString("s"));
        assertEquals(Verb.GO, Verb.fromString("south"));
    }

    @Test
    public void testInventorySynonyms() {
        assertEquals(Verb.INVENTORY, Verb.fromString("inventory"));
        assertEquals(Verb.INVENTORY, Verb.fromString("i"));
        assertEquals(Verb.INVENTORY, Verb.fromString("inv"));
    }

    @Test
    public void testUseSynonym() {
        assertEquals(Verb.USE, Verb.fromString("use"));
    }

    @Test
    public void testTalkSynonyms() {
        assertEquals(Verb.TALK, Verb.fromString("talk"));
    }

    @Test
    public void testPutSynonym() {
        assertEquals(Verb.PUT, Verb.fromString("put"));
    }

    @Test
    public void testSaveSynonym() {
        assertEquals(Verb.SAVE, Verb.fromString("save"));
    }

    @Test
    public void testLoadSynonym() {
        assertEquals(Verb.LOAD, Verb.fromString("load"));
    }

    @Test
    public void testQuitSynonyms() {
        assertEquals(Verb.QUIT, Verb.fromString("quit"));
        assertEquals(Verb.QUIT, Verb.fromString("exit"));
    }

    @Test
    public void testUnknownVerb() {
        assertEquals(Verb.UNKNOWN, Verb.fromString("blah"));
        assertEquals(Verb.UNKNOWN, Verb.fromString("xyz"));
    }

    @Test
    public void testCaseInsensitive() {
        assertEquals(Verb.LOOK, Verb.fromString("LOOK"));
        assertEquals(Verb.LOOK, Verb.fromString("Look"));
        assertEquals(Verb.TAKE, Verb.fromString("TAKE"));
    }
}
