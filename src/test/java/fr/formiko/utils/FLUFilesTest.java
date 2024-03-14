package fr.formiko.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class FLUFilesTest {
    private static String TEST_PATH = "src/test/resources/";
    private static String TEST_PATH_TEMPORARY = TEST_PATH + "tmp/";


    @AfterAll
    @BeforeAll
    static void clean() { FLUFiles.delete(TEST_PATH_TEMPORARY); }


    @ParameterizedTest
    @MethodSource("testIsAValidePathSource")
    void testIsAValidePath(String path, boolean isValide) { assertEquals(isValide, FLUFiles.isAValidePath(path)); }

    private static Stream<Arguments> testIsAValidePathSource() {
        return Stream.of(Arguments.of(".", true), Arguments.of("..", true), Arguments.of("~/", true), Arguments.of("../Utils/", true),
                Arguments.of(".gitignore", true), Arguments.of("", false), Arguments.of(null, false));
    }

    @ParameterizedTest
    @MethodSource("testCreateFilesSource")
    void testCreateFiles(String path, boolean shouldWork) {
        assertEquals(shouldWork, FLUFiles.createFile(path));
        if (shouldWork) {
            assertEquals(true, FLUFiles.delete(path));
        }
    }
    private static Stream<Arguments> testCreateFilesSource() {
        return Stream.of(Arguments.of(TEST_PATH_TEMPORARY + "testCreateFiles1.txt", true),
                Arguments.of(TEST_PATH_TEMPORARY + "testCreateFiles2.png", true), Arguments.of(TEST_PATH_TEMPORARY + "éà@--", true),
                Arguments.of(TEST_PATH + "existingFile.x", false), Arguments.of(TEST_PATH_TEMPORARY + "/2/3/4/t", true),
                Arguments.of(null, false));
    }

    @ParameterizedTest
    @MethodSource("testCreateDirectorySource")
    void testCreateDirectory(String path, boolean shouldWork) {
        assertEquals(shouldWork, FLUFiles.createDirectory(path));
        if (shouldWork) {
            assertEquals(true, FLUFiles.delete(path));
        }
    }
    private static Stream<Arguments> testCreateDirectorySource() {
        return Stream.of(Arguments.of(TEST_PATH_TEMPORARY + "testCreateDirectory1", true),
                Arguments.of(TEST_PATH_TEMPORARY + "testCreateDirectory2", true), Arguments.of(TEST_PATH_TEMPORARY + "éàOP%u%", true),
                Arguments.of(TEST_PATH, false), Arguments.of(TEST_PATH_TEMPORARY + "DIR/2/3/4/t/", true), Arguments.of(null, false));
    }

    @ParameterizedTest
    @MethodSource("testDeleteSource")
    void testDelete(String path, boolean shouldWork, String pathToCreate) {
        if (pathToCreate != null) {
            FLUFiles.createDirectory(pathToCreate);
        }
        assertEquals(shouldWork, FLUFiles.delete(path));
    }

    private static Stream<Arguments> testDeleteSource() {
        return Stream.of(Arguments.of(TEST_PATH_TEMPORARY, true, TEST_PATH_TEMPORARY + "P/2/3/4"),
                Arguments.of(TEST_PATH_TEMPORARY, false, null), Arguments.of(TEST_PATH + "unexistingDirectory", false, null),
                Arguments.of(null, false, null));
    }

    @ParameterizedTest
    @MethodSource("testCopySource")
    void testCopy(String source, boolean shouldWork, String destination) {
        assertEquals(shouldWork, FLUFiles.copy(source, destination));
        if (shouldWork) {
            assertEquals(true, FLUFiles.delete(destination));
        }
    }

    private static Stream<Arguments> testCopySource() {
        return Stream.of(Arguments.of(TEST_PATH + "existingFile.x", true, TEST_PATH_TEMPORARY + "existingFile.x"), // normal copy
                Arguments.of(TEST_PATH + "unexistingFile.x", false, TEST_PATH_TEMPORARY + "unexistingFile.x"), // copy of missing file
                Arguments.of(TEST_PATH_TEMPORARY + "existingFile.x", false, TEST_PATH_TEMPORARY + "existingFile.x"), // don't exist here
                Arguments.of(TEST_PATH + "existingFile.x", false, TEST_PATH + "existingFile.x"), // same location
                Arguments.of(null, false, TEST_PATH), Arguments.of(TEST_PATH + "existingFile.x", false, ""));
    }

    @ParameterizedTest
    @MethodSource("testMoveSource")
    void testMove(String source, boolean shouldWork, String destination) {
        if (source != null) {
            assertEquals(true, FLUFiles.createFile(source));
        }
        assertEquals(shouldWork, FLUFiles.move(source, destination));
        if (shouldWork) {
            assertEquals(true, FLUFiles.delete(destination));
        }
    }

    private static Stream<Arguments> testMoveSource() {
        return Stream.of(Arguments.of(TEST_PATH_TEMPORARY + "RTYUIFile.x", true, TEST_PATH_TEMPORARY + "DTCFile.x"),
                Arguments.of(null, false, TEST_PATH), Arguments.of(TEST_PATH_TEMPORARY + "exzbnkistingFile.x", false, null));
    }

    @ParameterizedTest
    @MethodSource("testCopyDirectorySource")
    void testCopyDirectory(String dirToCopy, boolean shouldWork, String destination, String fileIncideDirectory) {
        assertEquals(shouldWork, FLUFiles.copy(dirToCopy, destination));
        if (shouldWork) {
            assertTrue(new File(destination + fileIncideDirectory).exists());
            assertEquals(true, FLUFiles.delete(destination));
        }
    }

    private static Stream<Arguments> testCopyDirectorySource() {
        return Stream.of(
                Arguments.of(TEST_PATH + "existingDir/", true, TEST_PATH_TEMPORARY + "copyOfTestResources/", "subDir/existingFile.txt"),
                Arguments.of(TEST_PATH + "unexistingDirectory", false, TEST_PATH_TEMPORARY + "unexistingDirectory", null));
    }

    @ParameterizedTest
    @MethodSource("testMoveDirectorySource")
    void testMoveDirectory(String dirToMove, boolean shouldWork, String destination, String fileIncideDirectory) {
        String copyOfDirToMove = dirToMove.substring(0, dirToMove.length() - 2) + "-copy";
        assertTrue(FLUFiles.copy(dirToMove, copyOfDirToMove));
        assertEquals(shouldWork, FLUFiles.move(copyOfDirToMove, destination));
        if (shouldWork) {
            assertTrue(new File(destination + fileIncideDirectory).exists());
            // assertEquals(true, FLUFiles.delete(destination));
        }
    }

    private static Stream<Arguments> testMoveDirectorySource() {
        return Stream.of(
                Arguments.of(TEST_PATH + "existingDir/", true, TEST_PATH_TEMPORARY + "moveOfTestResources/", "subDir/existingFile.txt"));
    }

    @ParameterizedTest
    @MethodSource("testReadFileSource")
    void testReadFile(String path, boolean shouldWork, String content) {
        if (shouldWork) {
            assertEquals(content, FLUFiles.readFile(path));
        } else {
            assertNull(FLUFiles.readFile(path));
        }
    }

    private static Stream<Arguments> testReadFileSource() {
        return Stream.of(Arguments.of(TEST_PATH + "existingFile.x", true, "Some content."),
                Arguments.of(TEST_PATH + "unexistingFile.x", false, null), Arguments.of(null, false, null),
                Arguments.of(TEST_PATH + "existingDir/subDir/", false, null),
                Arguments.of(TEST_PATH + "existingDir/subDir/existingFile.txt", true, "ipnzéfl\n" + //
                        "zgrebinoa\n" + //
                        "rez bzn,\n"));
    }

    @ParameterizedTest
    @MethodSource("testReadFileAsListSource")
    void testWriteFile(String path, boolean shouldWork, List<String> content) {
        if (shouldWork) {
            List<String> list = FLUFiles.readFileAsList(path);
            assertEquals(content.size(), list.size());
            for (int i = 0; i < content.size(); i++) {
                assertEquals(content.get(i), list.get(i));
            }
        } else {
            assertNull(FLUFiles.readFileAsList(path));
        }
    }

    private static Stream<Arguments> testReadFileAsListSource() {
        return Stream.of(Arguments.of(TEST_PATH + "existingFile.x", true, List.of("Some content.")),
                Arguments.of(TEST_PATH + "unexistingFile.x", false, null), Arguments.of(null, false, null),
                Arguments.of(TEST_PATH + "existingDir/subDir/", false, null),
                Arguments.of(TEST_PATH + "existingDir/subDir/existingFile.txt", true, List.of("ipnzéfl", "zgrebinoa", "rez bzn,")));
    }

    @ParameterizedTest
    @MethodSource("testReadFileFromWebSource")
    void testReadFileFromWeb(String url, boolean shouldWork, String content) {
        if (shouldWork) {
            assertEquals(content, FLUFiles.readFileFromWeb(url));
        } else {
            assertNull(FLUFiles.readFileFromWeb(url));
        }
    }

    private static Stream<Arguments> testReadFileFromWebSource() { return Stream.of(Arguments.of(
            "https://gist.githubusercontent.com/HydrolienF/0dc21ed2c0788b4de206102871410d4b/raw/a85c8bcf47ae0c081841df756c68122c83151747/fr.json",
            true, """
                    {
                      "schemaVersion": 1,
                      "label": "French",
                      "message": "100%",
                      "color": "00FF00"
                    }"""), Arguments.of(null, false, null), Arguments.of("h://unexisting.url", false, null)); }

    @ParameterizedTest
    @MethodSource("testWriteFileSource")
    void testWriteFile(String path, boolean shouldWork, String content) {
        assertEquals(shouldWork, FLUFiles.writeFile(path, content));
        if (shouldWork) {
            assertEquals(content, FLUFiles.readFile(path));
            assertEquals(true, FLUFiles.delete(path));
        }
    }

    private static Stream<Arguments> testWriteFileSource() {
        return Stream.of(Arguments.of(TEST_PATH_TEMPORARY + "testWriteFile1.txt", true, "Some content."),
                Arguments.of(TEST_PATH_TEMPORARY + "testWriteFile2.txt", true, "Some content."),
                Arguments.of(TEST_PATH_TEMPORARY + "éà@--", true, "Some content."), Arguments.of(null, false, "Some vyzemjzefze"),
                Arguments.of(TEST_PATH_TEMPORARY + "DIR/2/out.in", true, "Some content"),
                Arguments.of(TEST_PATH_TEMPORARY + "existingFile2", true, "Some content"));
    }

    @ParameterizedTest
    @MethodSource("testAppendToFileSource")
    void testAppendToFile(String path, boolean shouldWork, String contentToWrite, String expectedContent) {
        assertEquals(shouldWork, FLUFiles.appendToFile(path, contentToWrite));
        if (shouldWork) {
            assertEquals(expectedContent, FLUFiles.readFile(path));
            assertEquals(true, FLUFiles.delete(path));
        }
    }

    private static Stream<Arguments> testAppendToFileSource() {
        return Stream.of(Arguments.of(TEST_PATH_TEMPORARY + "testAppendToFile1.txt", true, "Some content.", "Some content."),
                Arguments.of(TEST_PATH_TEMPORARY + "testAppendToFile2.txt", true, "Some content.", "Some content."),
                Arguments.of(TEST_PATH_TEMPORARY + "éà@--", true, "Some content.", "Some content."),
                Arguments.of(null, false, "Some vyzemjzefze", ""));
    }

    @ParameterizedTest
    @MethodSource("testAppendToExistingFileFileSource")
    void testAppendToExistingFile(String path, boolean shouldWork, String contentToWrite, String expectedContent, String copyPath) {
        assertEquals(true, FLUFiles.copy(path, copyPath));
        assertEquals(shouldWork, FLUFiles.appendToFile(copyPath, contentToWrite));
        if (shouldWork) {
            assertEquals(expectedContent, FLUFiles.readFile(copyPath));
        }
    }

    private static Stream<Arguments> testAppendToExistingFileFileSource() {
        return Stream.of(
                Arguments.of(TEST_PATH + "existingFile3", true, "Some content", "ABCSome content", TEST_PATH_TEMPORARY + "existingFile3"),
                Arguments.of(TEST_PATH + "existingFile4", true, "Some content", "ABC\nSome content",
                        TEST_PATH_TEMPORARY + "existingFile4"));
    }
}
