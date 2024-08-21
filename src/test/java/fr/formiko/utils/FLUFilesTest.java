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
                Arguments.of(".gitignore", true), Arguments.of("", true), Arguments.of(null, false));
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
                Arguments.of(null, false, TEST_PATH), Arguments.of(TEST_PATH + "existingFile.x", false, null));
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
                Arguments.of(TEST_PATH + "existingDir/subDir/existingFile.txt", true,
                        "ipnzéfl" + System.lineSeparator() + "zgrebinoa" + System.lineSeparator() + "rez bzn," + System.lineSeparator()));
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
                Arguments.of(TEST_PATH + "existingFile4", true, "Some content", "ABC" + System.lineSeparator() + "Some content",
                        TEST_PATH_TEMPORARY + "existingFile4"));
    }

    @ParameterizedTest
    @MethodSource("testListFilesSource")
    void testListFiles(String path, boolean shouldWork, List<String> expectedFiles) {
        clean();
        if (shouldWork) {
            List<String> files = FLUFiles.listFiles(path);
            assertEquals(expectedFiles.size(), files.size());
            for (String s : expectedFiles) {
                assertTrue(files.contains(s));
            }
        } else {
            assertNull(FLUFiles.listFiles(path));
        }
    }

    private static Stream<Arguments> testListFilesSource() {
        return Stream.of(
                Arguments.of(TEST_PATH, true,
                        List.of("existingDir/", "dir1/", "existingFile.x", "existingFile2", "existingFile3", "existingFile4")),
                Arguments.of(TEST_PATH + "existingDir/", true, List.of("subDir/")),
                Arguments.of(TEST_PATH + "existingDir/subDir/", true, List.of("existingFile.txt")), Arguments.of(null, false, null),
                Arguments.of(TEST_PATH + "unexistingDirectory", false, null));
    }

    @ParameterizedTest
    @MethodSource("testZipSource")
    void testZip(String path, boolean shouldWork, String destination, String realDestination) {
        assertEquals(shouldWork, FLUFiles.zip(path, destination));
        if (shouldWork) {
            assertEquals(true, FLUFiles.delete(realDestination));
        }
    }

    private static Stream<Arguments> testZipSource() {
        return Stream.of(
                Arguments.of(TEST_PATH + "existingDir/", true, TEST_PATH_TEMPORARY + "existingDir.zip",
                        TEST_PATH_TEMPORARY + "existingDir.zip"),
                Arguments.of(TEST_PATH + "existingDir", true, TEST_PATH_TEMPORARY + "existingDirZ2",
                        TEST_PATH_TEMPORARY + "existingDirZ2.zip"),
                Arguments.of(TEST_PATH + "unexistingDirectoryTYUI", false, TEST_PATH_TEMPORARY + "unexistingDirectory.zip", null),
                Arguments.of("existingDir", false, null, null), Arguments.of(null, false, null, null));
    }

    @ParameterizedTest
    @MethodSource("testZipWithExclusionSource")
    void testZipWithExclusion(String path, boolean shouldWork, String destination, String realDestination, List<String> exclusion,
            List<String> subItems) {
        assertEquals(shouldWork, FLUFiles.zip(path, destination, exclusion));
        if (shouldWork) {
            // unzip
            assertEquals(shouldWork, FLUFiles.unzip(destination, TEST_PATH_TEMPORARY + "TMP/", ""));
            // check if the files are here
            for (String s : subItems) {
                assertTrue(new File(TEST_PATH_TEMPORARY + "TMP/dir1/" + s).exists());
                assertTrue(FLUFiles.delete(TEST_PATH_TEMPORARY + "TMP/dir1/" + s));

            }
            assertTrue(FLUFiles.delete(realDestination));
        }
    }

    private static Stream<Arguments> testZipWithExclusionSource() {
        return Stream.of(
                Arguments.of(TEST_PATH + "existingDir/", true, TEST_PATH_TEMPORARY + "existingDir.zip",
                        TEST_PATH_TEMPORARY + "existingDir.zip", List.of(), List.of()),
                Arguments.of(TEST_PATH + "dir1/", true, TEST_PATH_TEMPORARY + "dir1.zip", TEST_PATH_TEMPORARY + "dir1.zip",
                        List.of("nonExistingFile.txt"), List.of("dir2/", "dir3/", "file4.md")),
                Arguments.of(TEST_PATH + "dir1/", true, TEST_PATH_TEMPORARY + "dir1.zip", TEST_PATH_TEMPORARY + "dir1.zip",
                        List.of("dir2/"), List.of("dir3/", "file4.md")));
    }

    @ParameterizedTest
    @MethodSource("testUnzipSource")
    void testUnzip(String pathToBeZip, boolean shouldWork, String zipedFile, String pathToDownloadIntoZip, String fileToCheck) {
        assertEquals(shouldWork, FLUFiles.zip(pathToBeZip, zipedFile));
        assertEquals(shouldWork, FLUFiles.unzip(zipedFile, TEST_PATH_TEMPORARY, pathToDownloadIntoZip));
        if (shouldWork) {
            assertTrue(new File(fileToCheck).exists());
            // assertEquals(true, FLUFiles.delete(zipedFile));
            // assertEquals(true, FLUFiles.delete(pathToDownloadIntoZip));
        }
        clean();
    }

    private static Stream<Arguments> testUnzipSource() {
        return Stream.of(
                Arguments.of(TEST_PATH + "existingDir/", true, TEST_PATH_TEMPORARY + "createdZip", "",
                        TEST_PATH_TEMPORARY + "existingDir/"),
                Arguments.of(TEST_PATH + "existingDir/", true, TEST_PATH_TEMPORARY + "createdZip", "existingDir/subDir/",
                        TEST_PATH_TEMPORARY + "subDir/existingFile.txt"),
                Arguments.of(TEST_PATH + "existingDir/", true, TEST_PATH_TEMPORARY + "createdZip", "existingDir/subDir/existingFile.txt",
                        TEST_PATH_TEMPORARY + "existingFile.txt"),
                Arguments.of(TEST_PATH + "existingDir/", true, TEST_PATH_TEMPORARY + "createdZip", "existingDir/subDir/*",
                        TEST_PATH_TEMPORARY + "existingFile.txt"));
    }

    @ParameterizedTest
    @MethodSource("testDownloadSource")
    void testDownload(String url, boolean shouldWork, String destination, String fileToCheck) {
        assertEquals(shouldWork, FLUFiles.download(url, destination));
        if (shouldWork) {
            assertTrue(new File(fileToCheck).exists());
            assertEquals(true, FLUFiles.delete(destination));
        }
    }

    private static Stream<Arguments> testDownloadSource() {
        return Stream.of(Arguments.of(
                "https://gist.githubusercontent.com/HydrolienF/0dc21ed2c0788b4de206102871410d4b/raw/a85c8bcf47ae0c081841df756c68122c83151747/fr.json",
                true, TEST_PATH_TEMPORARY + "fr.json", TEST_PATH_TEMPORARY + "fr.json"),
                Arguments.of("https://unexisting.url", false, TEST_PATH_TEMPORARY + "unexisting.url", null));
    }

    @ParameterizedTest
    @MethodSource("testDownloadAndUnzipSource")
    void testDownloadAndUnzip(String url, boolean shouldWork, String destination, String fileToCheck, String directoryInsideZipToGet) {
        assertEquals(shouldWork, FLUFiles.downloadAndUnzip(url, destination, directoryInsideZipToGet));
        if (shouldWork) {
            assertTrue(new File(fileToCheck).exists());
            assertEquals(true, FLUFiles.delete(destination));
        }
    }

    private static Stream<Arguments> testDownloadAndUnzipSource() {
        return Stream.of(
                Arguments.of("https://github.com/HydrolienF/Kokcinelo/releases/download/3.0.20/KokcineloLauncher.zip", true,
                        TEST_PATH_TEMPORARY + "kl1/", TEST_PATH_TEMPORARY + "kl1/" + "Kokcinelo3.0.20/", "Kokcinelo3.0.20/"),
                Arguments.of("https://github.com/HydrolienF/Kokcinelo/releases/download/3.0.20/KokcineloLauncher.zip", true,
                        TEST_PATH_TEMPORARY + "kl2/", TEST_PATH_TEMPORARY + "kl2/" + "", "Kokcinelo3.0.20/"),
                Arguments.of("https://github.com/HydrolienF/Kokcinelo/releases/download/3.0.20/KokcineloLauncher.zip", true,
                        TEST_PATH_TEMPORARY + "kl3/", TEST_PATH_TEMPORARY + "kl3/" + "Kokcinelo3.0.20", "Kokcinelo3.0.20/"),
                Arguments.of("https://github.com/HydrolienF/Kokcinelo/releases/download/3.0.20/KokcineloLauncher.zip", true,
                        TEST_PATH_TEMPORARY + "kl4/", TEST_PATH_TEMPORARY + "kl4/" + "icon.png", "Kokcinelo3.0.20/icon.png"),
                Arguments.of("https://github.com/HydrolienF/Kokcinelo/releases/download/3.0.20/KokcineloLauncher.zip", true,
                        TEST_PATH_TEMPORARY + "kl5/", TEST_PATH_TEMPORARY + "kl5/" + "icon.ico", "Kokcinelo3.0.20/icon.ico"),
                Arguments.of("https://github.com/HydrolienF/Formiko/releases/download/2.29.23/Formiko2.29.23Linux.zip", true,
                        TEST_PATH_TEMPORARY + "kl6/", TEST_PATH_TEMPORARY + "kl6/" + "java/", "Formiko2.29.23Linux/java/"),
                Arguments.of("https://github.com/HydrolienF/Formiko/releases/download/2.29.23/Formiko2.29.23Linux.zip", true,
                        TEST_PATH_TEMPORARY + "kl6/", TEST_PATH_TEMPORARY + "kl6/" + "java", "Formiko2.29.23Linux/java"),
                Arguments.of("https://unexisting.url", false, TEST_PATH_TEMPORARY + "unexisting.url", null, ""));
    }

    @ParameterizedTest
    @MethodSource("testCountEntryOfZipFileSource")
    void testCountEntryOfZipFile(String url, int expectedCount) { assertEquals(expectedCount, FLUFiles.countEntryOfZipFile(url)); }

    private static Stream<Arguments> testCountEntryOfZipFileSource() {
        return Stream.of(Arguments.of("https://github.com/HydrolienF/Kokcinelo/releases/download/3.0.20/KokcineloLauncher.zip", 9),
                Arguments.of("https://github.com/HydrolienF/Formiko/releases/download/2.29.23/Formiko2.29.23Linux.zip", 9),
                Arguments.of("https://unexisting.url", -1));
    }


    public static void main(String[] args) {
        // FLUFiles.setProgression(new FLUProgressionCLI());
        // FLUFiles.createFile(TEST_PATH_TEMPORARY + "/testCreateFiles1.txt");
        // Arguments.of("https://github.com/HydrolienF/Kokcinelo/releases/download/3.0.20/KokcineloLauncher.zip", true,
        // TEST_PATH_TEMPORARY + "kl1/", TEST_PATH_TEMPORARY + "kl1/" + "KokcineloLauncher/", "")
        // clean();
        // System.out
        // .println(FLUFiles.downloadAndUnzip("https://github.com/HydrolienF/Kokcinelo/releases/download/3.0.20/KokcineloLauncher.zip",
        // TEST_PATH_TEMPORARY, "Kokcinelo3.0.20/icon.png"));

        clean();
        // FLUFiles.zip("../teavm/", "teavm.zip");
        // FLUFiles.unzip("teavm.zip", TEST_PATH_TEMPORARY, "teavm/jso/*");
        // FLUFiles.zip("C:\\Users\\Hydrolien\\git\\paper.1.21\\", "C:\\Users\\Hydrolien\\git\\paper.1.21\\saves\\1.zip",
        // List.of("saves/", "cache/"));
        // FLUFiles.zip("C:/Users/Hydrolien/git/paper.1.21/", "C:/Users/Hydrolien/git/paper.1.21/save/1.zip");
        FLUFiles.zip("C:/Users/Hydrolien/git/paper.1.21/", "C:/Users/Hydrolien/git/paper.1.21/saves/" + FLUTime.currentTime() + ".zip",
                List.of("saves/", "cache/"));
    }
}
