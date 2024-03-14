package fr.formiko.utils.progressions;

import fr.formiko.utils.FLUFiles;

/**
 * {@summary Simple CLI view that are update as a progression for downloading files.}<br>
 */
public class FLUProgressionCLI implements FLUProgression {
    @Override
    public void iniLauncher() { FLUFiles.setProgression(this); }
    @Override
    public void setDownloadingMessage(String message) { System.out.println("Dowload: " + message); }
    @Override
    public void setDownloadingValue(int value) { System.out.println(value + "% done"); }
}
