package fr.formiko.utils;

import javax.annotation.Nonnull;

/**
 * A simple utility class to determine the current operating system.
 * 
 * @author Hydrolien
 * @since 0.0.1
 * @version 0.0.1
 */
public class FLUOS {
    private static FLUOS instance = new FLUOS();
    private final OS os;

    /**
     * Singleton pattern.
     * Private constructor to prevent instantiation.
     */
    private FLUOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            os = OS.WINDOWS;
        } else if (osName.contains("mac")) {
            os = OS.MAC;
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            os = OS.LINUX;
        } else {
            os = OS.OTHER;
        }
    }

    public static @Nonnull OS getOS() { return instance.os; }
    public static boolean isWindows() { return instance.os == OS.WINDOWS; }
    public static boolean isLinux() { return instance.os == OS.LINUX; }
    public static boolean isMac() { return instance.os == OS.MAC; }
    public static boolean isOther() { return instance.os == OS.OTHER; }

    enum OS {
        WINDOWS, LINUX, MAC, OTHER
    }
}
