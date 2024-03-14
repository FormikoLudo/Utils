package fr.formiko.utils.progressions;

/**
 * {@summary Update a progression state.}
 * 
 * @since 0.0.4
 * @author Hydrolien
 */
public interface FLUProgression {
    /***
     * {@summary Update downloading message.}
     * 
     * @param message the message
     */
    default void setDownloadingMessage(String message) {}
    /***
     * {@summary Update downloading %age.}
     * 
     * @param state the state as a %age
     */
    default void setDownloadingValue(int state) {}
    /***
     * {@summary Initialize the game launcher.}
     */
    default void iniLauncher() {}
    /***
     * {@summary Close the game launcher.}
     */
    default void closeLauncher() {}
    /***
     * {@summary Hide or show buttonRetry of FFrameLauncher.}
     */
    default void setButtonRetryVisible(boolean visible) {}
}
