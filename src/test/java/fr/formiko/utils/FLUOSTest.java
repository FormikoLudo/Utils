package fr.formiko.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class FLUOSTest {
    @Test
    void testNull() { assertNotNull(FLUOS.getOS()); }

    @Test
    void testIsX() { assertTrue(FLUOS.isWindows() || FLUOS.isLinux() || FLUOS.isMac() || FLUOS.isOther()); }
}
