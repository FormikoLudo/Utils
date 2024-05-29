package fr.formiko.utils;

import fr.formiko.utils.progressions.FLUProgression;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * A utility class to manipulate files.
 * Most functions don't have a comment because name should be obvious.
 * When a function name does not contains file or directory, it can be used with both.
 * All functions return true if it work and false if it doesn't.
 * Long opperation that are multithreaded:
 * - zip
 * - unzip
 * - copy
 * 
 * @author Hydrolien
 * @since 0.0.3
 * @version 0.0.4
 */
public class FLUFiles {
    private static FLUFilesInternal internal = new FLUFilesInternal();
    private static FLUProgression progression;
    private static final String FILE_SEPARATOR = "/";

    private FLUFiles() {} // hide constructor

    // GET SET ----------------------------------------------------------------------
    public static FLUProgression getProgression() { return progression; }
    public static void setProgression(FLUProgression progression) { FLUFiles.progression = progression; }
    private static void setDownloadingValue(double value) {
        if (progression != null) {
            progression.setDownloadingValue(value);
        }
    }
    private static void setDownloadingMessage(String message) {
        if (progression != null) {
            progression.setDownloadingMessage(message);
        }
    }

    // TODO add progressions to all functions that might be long opperation:
    // delete, copy, move, readFile, readFileAsList, readFileFromWeb, writeFile, appendToFile, listFiles, zip, unzip, download,
    // downloadAndUnzip, downloadAndRead, countEntryOfZipFile, getSize

    public static boolean isAValidePath(String path) { return internal.isAValidePath(path); }
    public static boolean createFile(String path) { return internal.createFile(path); }
    public static boolean createDirectory(String path) { return internal.createDirectory(path); }
    public static boolean delete(String path) { return internal.delete(path); }
    public static boolean copy(String source, String destination) { return internal.copy(source, destination); }
    public static boolean move(String source, String destination) { return internal.move(source, destination); }

    public static String readFile(String path) { return internal.readFile(path); }
    public static List<String> readFileAsList(String path) { return internal.readFileAsList(path); }
    public static String readFileFromWeb(String url) { return internal.readFileFromWeb(url); }
    public static boolean writeFile(String path, String content) { return internal.writeFile(path, content); }
    public static boolean appendToFile(String path, String content) { return internal.appendToFile(path, content); }

    public static List<String> listFiles(String path) { return internal.listFiles(path); }

    public static boolean zip(String source, String destination) { return internal.zip(source, destination); }
    public static boolean unzip(String source, String destination, String directoryInsideZipToGet) {
        return internal.unzip(source, destination, directoryInsideZipToGet);
    }
    public static boolean unzip(String source, String destination) { return unzip(source, destination, ""); }

    public static boolean download(String url, String destination) { return internal.download(url, destination); }
    public static boolean downloadAndUnzip(String url, String destination, String directoryInsideZipToGet) {
        return internal.downloadAndUnzip(url, destination, directoryInsideZipToGet);
    }
    public static boolean downloadAndUnzip(String url, String destination) { return downloadAndUnzip(url, destination, ""); }
    public static String downloadAndRead(String url) { return readFileFromWeb(url); }
    public static int countEntryOfZipFile(String url) { return internal.countEntryOfZipFile(url); }
    public static long getSize(String path) { return -1; }

    public static boolean setMaxPermission(String path, boolean recursive) { return false; }
    public static boolean setMaxPermission(String path) { return setMaxPermission(path, true); }

    public static boolean openWebLinkInBrowser(String url) { return false; }

    // Internal class to hide implementation
    private static class FLUFilesInternal {
        private FLUFilesInternal() {} // hide constructor

        private boolean isAValidePath(String path) { return path != null; }

        private boolean createFile(String path) {
            if (isAValidePath(path)) {
                try {
                    // int actionToDo = 3;
                    // int currentAction = 0;
                    // setDownloadingValue(currentAction++ / (double) actionToDo);
                    // setDownloadingMessage("Creating file object for " + path);
                    File file = new File(path);
                    // setDownloadingValue(currentAction++ / (double) actionToDo);
                    // setDownloadingMessage("Creating parents");
                    createParents(file);
                    // setDownloadingValue(currentAction++ / (double) actionToDo);
                    // setDownloadingMessage("Creating file");
                    boolean r = file.createNewFile();
                    // setDownloadingValue(currentAction++ / (double) actionToDo);
                    return r;
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
                        delete(path + FILE_SEPARATOR + subPath);
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
                createParents(destinationFile);
                File sourceFile = new File(source);
                if (!sourceFile.exists()) {
                    return false;
                }
                if (sourceFile.isDirectory()) {
                    destinationFile.mkdirs();
                    Arrays.stream(sourceFile.list()).parallel()
                            .forEach(subPath -> copy(source + FILE_SEPARATOR + subPath, destination + FILE_SEPARATOR + subPath));
                    return true;
                }
                try {
                    com.google.common.io.Files.copy(sourceFile, destinationFile);
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
                createParents(destinationFile);
                return new File(source).renameTo(destinationFile);
            } else {
                return false;
            }
        }
        private String readFile(String path) {
            if (isAValidePath(path)) {
                try {
                    // return Files.readAllLines(Paths.get(path));
                    return Files.readString(Paths.get(path));
                } catch (IOException e) {
                    return null;
                }
            } else {
                return null;
            }
        }
        private List<String> readFileAsList(String path) {
            if (isAValidePath(path)) {
                try {
                    return Files.readAllLines(Paths.get(path));
                } catch (IOException e) {
                    return null;
                }
            } else {
                return null;
            }
        }

        private String readFileFromWeb(String urlString) {
            if (urlString == null) {
                return null;
            }
            try {
                URL url = URI.create(urlString).toURL();
                try (InputStream is = url.openStream()) {
                    return new String(is.readAllBytes());
                }
            } catch (IOException e) {
                return null;
            }
        }

        /**
         * It will create the file and it's path if needed.
         */
        private boolean writeFile(String path, String content) {
            if (isAValidePath(path)) {
                createParents(path);
                try {
                    Files.writeString(Paths.get(path), content, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                    return true;
                } catch (IOException e) {
                    return false;
                }
            } else {
                return false;
            }
        }

        /**
         * It will create the file and it's path if needed.
         */
        private boolean appendToFile(String path, String content) {
            if (isAValidePath(path)) {
                createParents(path);
                try {
                    Files.writeString(Paths.get(path), content, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                    return true;
                } catch (IOException e) {
                    return false;
                }
            } else {
                return false;
            }
        }

        private List<String> listFiles(String path) {
            if (isAValidePath(path)) {
                String[] list = new File(path).list();
                return list == null ? null : Arrays.asList(list);
            } else {
                return null;
            }
        }

        private boolean zip(String source, String destination) {
            if (isAValidePath(source) && isAValidePath(destination)) {
                destination = FLUStrings.addAtTheEndIfNeeded(destination, ".zip");
                createParents(destination);
                File sourceFile = new File(source);
                if (!sourceFile.exists()) {
                    return false;
                }
                try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(Paths.get(destination)))) {
                    zipFile(sourceFile, sourceFile.getName(), destination, zos);
                    return true;
                } catch (IOException e) {
                    return false;
                }
            } else {
                return false;
            }
        }
        private void zipFile(File fileToZip, String fileName, String destination, ZipOutputStream zos) throws IOException {
            if (fileToZip.isDirectory()) {
                final String finalfileName = FLUStrings.addAtTheEndIfNeeded(fileName, FILE_SEPARATOR);
                zos.putNextEntry(new ZipEntry(finalfileName));
                zos.closeEntry();
                List<Boolean> allOk = Arrays.asList(true);
                Arrays.stream(fileToZip.listFiles()).parallel().forEach(file -> {
                    try {
                        zipFile(file, finalfileName + file.getName(), destination, zos);
                    } catch (IOException e) {
                        allOk.set(0, false);
                    }
                });
                if (!allOk.getFirst()) {
                    throw new IOException("Error while zipping");
                }
            } else {
                zos.putNextEntry(new ZipEntry(fileName));
                Files.copy(fileToZip.toPath(), zos);
                zos.closeEntry();
            }
        }
        private void createParents(String path) { createParents(new File(path)); }
        private void createParents(File file) {
            if (file != null) {
                file.getParentFile().mkdirs();
            }
        }

        private boolean unzip(String source, String destination, String directoryInsideZipToGet) {
            if (isAValidePath(source)) {
                source = FLUStrings.addAtTheEndIfNeeded(source, ".zip");
                return unzipAllEntries(source, destination, directoryInsideZipToGet);
            } else {
                return false;
            }
        }

        private boolean unzip(InputStream source, String destination, String directoryInsideZipToGet) {
            if (isAValidePath(destination)) {
                File destinationFile = new File(destination);
                createParents(destinationFile);
                try (ZipInputStream zis = new ZipInputStream(source)) {
                    boolean allOk = true;
                    for (ZipEntry entry = zis.getNextEntry(); entry != null; entry = zis.getNextEntry()) {
                        if (!createZipEntry(destination, directoryInsideZipToGet, zis, entry)) {
                            allOk = false;
                        }
                    }
                    return allOk;
                } catch (Exception e) {
                    System.out.println(e);
                    e.printStackTrace();
                    return false;
                }
            } else {
                return false;
            }
        }
        private boolean unzipAllEntries(String source, String destination, String directoryInsideZipToGet) {
            try (ZipFile zipFile = new ZipFile(source)) {
                final List<Boolean> allOk = Arrays.asList(true);
                zipFile.stream().parallel().forEach(entry -> {
                    try {
                        createZipEntry(destination, directoryInsideZipToGet, zipFile.getInputStream(entry), entry);
                    } catch (IOException e) {
                        allOk.set(0, false);
                    }
                });
                return allOk.get(0);
            } catch (IOException e) {
                return false;
            }
        }

        private boolean createZipEntry(String destination, String directoryInsideZipToGet, InputStream zis, ZipEntry entry)
                throws IOException {
            directoryInsideZipToGet = FLUStrings.removeAtTheEndIfNeeded(directoryInsideZipToGet.replace('\\', '/'), FILE_SEPARATOR);
            File destinationFile = new File(destination);
            String absoluteDestinationPath = FLUStrings.addAtTheEndIfNeeded(destinationFile.getAbsolutePath().replace('\\', '/'),
                    FILE_SEPARATOR);
            String entryName = entry.getName().replace('\\', '/');
            if (directoryInsideZipToGet.isEmpty() || directoryInsideZipToGet.equals(".") || entryName.startsWith(directoryInsideZipToGet)) {
                // Remove part of the path that we don't want
                int charToCut = directoryInsideZipToGet.lastIndexOf('/');
                entryName = FLUStrings.removeAtTheBeginningIfNeeded(entryName.substring(Math.max(0, charToCut)), FILE_SEPARATOR);

                if (entryName.isEmpty()) {
                    return true;
                }
                File fileToCreate = new File(destination, entryName);
                if (fileToCreate.getAbsolutePath().replace('\\', '/').startsWith(absoluteDestinationPath)) {
                    if (entry.isDirectory()) {
                        if (!createDirectory(absoluteDestinationPath + entryName)) {
                            return false;
                        }
                        return true;
                    } else {
                        createParents(absoluteDestinationPath + entryName);
                        return Files.copy(zis, Paths.get(absoluteDestinationPath + entryName)) >= 0L;
                    }
                }
            }
            return true;
        }

        private boolean download(String url, String destination) {
            if (isAValidePath(url) && isAValidePath(destination)) {
                try {
                    URL urlObject = URI.create(url).toURL();
                    InputStream is = urlObject.openStream();
                    File destinationFile = new File(destination);
                    createParents(destinationFile);
                    Files.copy(is, destinationFile.toPath());
                    return true;
                } catch (IOException e) {
                    return false;
                }
            } else {
                return false;
            }
        }

        private boolean downloadAndUnzip(String url, String destination, String directoryInsideZipToGet) {
            if (isAValidePath(url)) {
                url = FLUStrings.addAtTheEndIfNeeded(url, ".zip");
                try {
                    return unzip(URI.create(url).toURL().openStream(), destination, directoryInsideZipToGet);
                } catch (IOException e) {
                    return false;
                }
            } else {
                return false;
            }
        }

        private int countEntryOfZipFile(String url) {
            if (isAValidePath(url)) {
                try (ZipInputStream zis = new ZipInputStream(URI.create(url).toURL().openStream())) {
                    int count = 0;
                    while (zis.getNextEntry() != null) {
                        count++;
                    }
                    return count;
                } catch (IOException e) {
                    return -1;
                }
            } else {
                return -1;
            }
        }


        class FLUProgressionThread extends Thread {
            private File fileOut;
            private long fileToDowloadSize;
            private boolean running;
            private String downloadName;
            private FLUProgression progressionInstance;
            /**
             * {@summary Main constructor.}<br>
             * 
             * @param fileOut           file that we are curently filling by the downloading file
             * @param fileToDowloadSize size that we should reach when download will end
             */
            public FLUProgressionThread(File fileOut, long fileToDowloadSize, String downloadName, FLUProgression progressionInstance) {
                this.fileOut = fileOut;
                this.fileToDowloadSize = fileToDowloadSize;
                this.downloadName = downloadName;
                this.progressionInstance = progressionInstance;
                running = true;
            }

            public void stopRuning() { running = false; }
            /**
             * {@summary Main function that print every second %age of download done.}<br>
             */
            public void run() {
                long fileOutSize = 0;
                long lastFileOutSize = 0;
                long timeStart = System.currentTimeMillis();
                long timeFromLastBitDownload = timeStart;
                while (fileOutSize < fileToDowloadSize && running) {
                    fileOutSize = fileOut.length();
                    double progression = ((double) fileOutSize) / (double) fileToDowloadSize;
                    int percent = (int) (100 * progression);
                    long curentTime = System.currentTimeMillis();
                    long timeElapsed = curentTime - timeStart;
                    long timeLeft = (long) ((double) ((timeElapsed / progression) - timeElapsed));
                    String sTimeLeft = FLUTime.msToTime(timeLeft) + " left";
                    String message = "Downloading " + downloadName + " - " + percent + "% - ";
                    if (fileOutSize != lastFileOutSize) {// update watcher of working download
                        timeFromLastBitDownload = curentTime;
                    }
                    if (timeFromLastBitDownload + 10000 < curentTime) {
                        message += (((curentTime - timeFromLastBitDownload) / 1000) + "s untill a new bit haven't been download");
                        if (timeFromLastBitDownload + 60000 < curentTime) {
                            stopRuning();
                            // TODO stop the IO opperation.
                        }
                    } else {
                        message += sTimeLeft;
                    }
                    progressionInstance.setDownloadingValue(percent);
                    progressionInstance.setDownloadingMessage(message);

                    lastFileOutSize = fileOutSize;
                    FLUTime.sleep(50);
                }
            }
        }
    }
}
