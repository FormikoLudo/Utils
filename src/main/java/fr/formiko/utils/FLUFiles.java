package fr.formiko.utils;

import java.util.List;

/**
 * A utility class to manipulate files.
 * 
 * @author Hydrolien
 * @since 0.0.3
 * @version 0.0.3
 */
public class FLUFiles {
    private static FLUFilesInternal internal = new FLUFilesInternal();
    private FLUFiles() {} // hide constructor

    // public @Nullable String readFile(File file) { return null; }

    public static boolean createFile(String path) { return false; }
    public static boolean createDirectory(String path) { return false; }
    public static boolean delete(String path) { return false; }
    public static boolean copy(String source, String destination) { return false; }
    public static boolean move(String source, String destination) { return false; }

    public static String readFile(String path) { return null; }
    public static List<String> readFileAsList(String path) { return null; }
    public static boolean writeFile(String path, String content) { return false; }
    public static boolean appendToFile(String path, String content) { return false; }

    public static List<String> listFiles(String path) { return null; }

    public static boolean zip(String source, String destination) { return false; }
    public static boolean unzip(String source, String destination, String folderIncideZipToGet) { return false; }
    public static boolean unzip(String source, String destination) { return unzip(source, destination, "."); }

    public static boolean download(String url, String destination, boolean withProgressInfo) { return false; }
    public static boolean download(String url, String destination) { return download(url, destination, false); }
    public static boolean downloadAndUnzip(String url, String destination, String folderIncideZipToGet) { return false; }
    public static boolean downloadAndUnzip(String url, String destination) { return downloadAndUnzip(url, destination, "."); }
    public static String downloadAndRead(String url) { return null; }
    public static int countEntryOfZipFile(String url) { return -1; }
    public static long getSize(String path) { return -1; }

    public static boolean setMaxPermission(String path, boolean recursive) { return false; }
    public static boolean setMaxPermission(String path) { return setMaxPermission(path, true); }

    public static boolean openWebLinkInBrowser(String url) { return false; }


    private static class FLUFilesInternal {
        private FLUFilesInternal() {} // hide constructor
        // private @Nullable String readFile(File file) {
        // if (file == null || !file.exists() || !file.isFile()) {
        // return null;
        // } else {
        // Files.readLines(file, null);
        // }
        // }
        // private @Nullable String readInputStream(InputStream is) {
        // try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
        // return br.lines().collect(Collectors.joining(System.lineSeparator()));
        // } catch (IOException e) {
        // return null;
        // }
        // }
    }
}
