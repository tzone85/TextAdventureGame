package io.vxd.textadventure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Smoke test to verify Maven Surefire is properly configured and can run tests.
 */
public class SmokeTest {

    @Test
    public void shouldPass() {
        assertTrue(true, "This test should always pass to verify Surefire is working");
    }
}