package fr.formiko.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class FLUTimeTest {
    @Test
    void testCurrentTime() {
        // assertEquals(expected, FLUStrings.addAtTheEndIfNeeded(string, toAdd));
        String result = FLUTime.currentTime();
        assertEquals(23, result.length());
        // Test that the result have only number, ' ', '-' and '.'
        assertTrue(result.matches("[0-9\\-\\s\\.]{" + result.length() + "}"));
    }

}
