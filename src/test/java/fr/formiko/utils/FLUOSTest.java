package fr.formiko.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class FLUOSTest {
    @Test
    void testNull() { assertNotNull(FLUOS.getOS()); }

    @Test
    void testIsX() { assertTrue(FLUOS.isWindows() || FLUOS.isLinux() || FLUOS.isMac() || FLUOS.isOther()); }

    @ParameterizedTest
    @CsvSource({"Windowsx64, true, false, false, false", "Debian-Linux, false, true, false, false", "MacOS56, false, false, true, false",
            "X, false, false, false, true", "Onix, false, true, false, false", "maix, false, true, false, false"})
    void testOS(String osName, boolean isWindows, boolean isLinux, boolean isMac, boolean isOther) {
        assertEquals(isWindows, new FLUOSTestInstance(osName).isWindows());
        assertEquals(isLinux, new FLUOSTestInstance(osName).isLinux());
        assertEquals(isMac, new FLUOSTestInstance(osName).isMac());
        assertEquals(isOther, new FLUOSTestInstance(osName).isOther());
    }

    class FLUOSTestInstance extends FLUOS {
        public FLUOSTestInstance(String osName) { super(osName.toLowerCase()); }
    }
}
