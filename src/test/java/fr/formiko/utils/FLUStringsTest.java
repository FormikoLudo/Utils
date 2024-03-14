package fr.formiko.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class FLUStringsTest {
    @ParameterizedTest
    @MethodSource("testAddAtTheEndIfNeededSource")
    public void testAddAtTheEndIfNeeded(String string, String toAdd, String expected) {
        assertEquals(expected, FLUStrings.addAtTheEndIfNeeded(string, toAdd));
    }

    private static Stream<Arguments> testAddAtTheEndIfNeededSource() {
        return Stream.of(Arguments.of("test", "test", "test"), Arguments.of("test1", "test", "test1test"),
                Arguments.of("OPJ", ".txt", "OPJ.txt"), Arguments.of(null, ".txt", ".txt"), Arguments.of("A", null, "A"));
    }
}
