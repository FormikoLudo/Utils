package fr.formiko.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import com.google.common.io.Files;

/**
 * A utility class to manipulate files.
 * Most functions don't have a comment because name should be obvious.
 * When a function name does not contains file or directory, it can be used with both.
 * All functions return true if it work and false if it doesn't.
 * 
 * @author Hydrolien
 * @since 0.0.3
 * @version 0.0.3
 */
public class FLUFiles {
    private static FLUFilesInternal internal = new FLUFilesInternal();

    private FLUFiles() {} // hide constructor

    public static boolean isAValidePath(String path) { return internal.isAValidePath(path); }
    public static boolean createFile(String path) { return internal.createFile(path); }
    public static boolean createDirectory(String path) { return internal.createDirectory(path); }
    public static boolean delete(String path) { return internal.delete(path); }
    public static boolean copy(String source, String destination) { return internal.copy(source, destination); }
    public static boolean move(String source, String destination) { return internal.move(source, destination); }

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

    // Internal class to hide implementation
    private static class FLUFilesInternal {
        private FLUFilesInternal() {} // hide constructor

        private static boolean isAValidePath(String path) { return path != null && !path.isBlank(); }

        private boolean createFile(String path) {
            if (isAValidePath(path)) {
                try {
                    File file = new File(path);
                    file.getParentFile().mkdirs();
                    return file.createNewFile();
                } catch (IOException e) {
                    return false;
                }
            } else {
                return false;
            }
        }

        private boolean createDirectory(String path) {
            if (isAValidePath(path)) {
                return new File(path).mkdirs();
            } else {
                return false;
            }
        }

        private boolean delete(String path) {
            if (isAValidePath(path)) {
                File f = new File(path);
                if (f.isDirectory()) {
                    for (String subPath : f.list()) {
                        delete(path + File.separator + subPath);
                    }
                }
                return f.delete();
            } else {
                return false;
            }
        }
        /**
         * Copy a file or a directory.
         */
        private boolean copy(String source, String destination) {
            if (isAValidePath(source) && isAValidePath(destination)) {
                File destinationFile = new File(destination);
                destinationFile.getParentFile().mkdirs();
                File sourceFile = new File(source);
                if (!sourceFile.exists()) {
                    return false;
                }
                if (sourceFile.isDirectory()) {
                    destinationFile.mkdirs();
                    for (String subPath : sourceFile.list()) {
                        copy(source + File.separator + subPath, destination + File.separator + subPath);
                    }
                    return true;
                }
                try {
                    Files.copy(sourceFile, destinationFile);
                } catch (IOException | IllegalArgumentException e) {
                    return false;
                }
                // For performance reason, we don't check if content is the same, if there were no exception, it should be the same.
                return true;
            } else {
                return false;
            }
        }


        private boolean move(String source, String destination) {
            if (isAValidePath(source) && isAValidePath(destination)) {
                File destinationFile = new File(destination);
                destinationFile.getParentFile().mkdirs();
                return new File(source).renameTo(destinationFile);
            } else {
                return false;
            }
        }
    }
}
