package fr.formiko.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class FLUStringsTest {
    @ParameterizedTest
    @MethodSource("testAddAtTheEndIfNeededSource")
    void testAddAtTheEndIfNeeded(String string, String toAdd, String expected) {
        assertEquals(expected, FLUStrings.addAtTheEndIfNeeded(string, toAdd));
    }

    private static Stream<Arguments> testAddAtTheEndIfNeededSource() {
        return Stream.of(Arguments.of("test", "test", "test"), Arguments.of("test1", "test", "test1test"),
                Arguments.of("OPJ", ".txt", "OPJ.txt"), Arguments.of(null, ".txt", ".txt"), Arguments.of("A", null, "A"));
    }

    @ParameterizedTest
    @MethodSource("testRemoveAtTheEndIfNeededSource")
    void testRemoveAtTheEndIfNeeded(String string, String toRemove, String expected) {
        assertEquals(expected, FLUStrings.removeAtTheEndIfNeeded(string, toRemove));
    }

    private static Stream<Arguments> testRemoveAtTheEndIfNeededSource() {
        return Stream.of(Arguments.of("test", "test", ""), Arguments.of("test1", "test", "test1"), Arguments.of("OPJ.txt", ".txt", "OPJ"),
                Arguments.of(null, ".txt", ""), Arguments.of("A", null, "A"));
    }

    @ParameterizedTest
    @MethodSource("testAddAtTheBeginningIfNeededSource")
    void testAddAtTheBeginningIfNeeded(String string, String toAdd, String expected) {
        assertEquals(expected, FLUStrings.addAtTheBeginningIfNeeded(string, toAdd));
    }

    private static Stream<Arguments> testAddAtTheBeginningIfNeededSource() {
        return Stream.of(Arguments.of("test", "test", "test"), Arguments.of("1test", "test", "test1test"),
                Arguments.of("OPJ", "C:", "C:OPJ"), Arguments.of(null, "C:", "C:"), Arguments.of("A", null, "A"));
    }

    @ParameterizedTest
    @MethodSource("testRemoveAtTheBeginningIfNeededSource")
    void testRemoveAtTheBeginningIfNeeded(String string, String toRemove, String expected) {
        assertEquals(expected, FLUStrings.removeAtTheBeginningIfNeeded(string, toRemove));
    }

    private static Stream<Arguments> testRemoveAtTheBeginningIfNeededSource() {
        return Stream.of(Arguments.of("test", "test", ""), Arguments.of("test1", "test", "1"), Arguments.of("C:OPJ", "C:", "OPJ"),
                Arguments.of(null, "C:", ""), Arguments.of("A", null, "A"));
    }
}
