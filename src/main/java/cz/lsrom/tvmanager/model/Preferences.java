package cz.lsrom.tvmanager.model;

/**
 * Created by lsrom on 11/17/16.
 */
public class Preferences {
    /* Structure of the new filename. Each token is replaced by it's proper value from TheTVDB. */
    public String replacementString = ReplacementToken.SHOW_NAME + " s" + ReplacementToken.SEASON_NUM + "e" + ReplacementToken.EPISODE_NUM + " " + ReplacementToken.EPISODE_TITLE;

    /* Default location where should FileChooser open. */
    public String defaultFileChooserOpenLocation = System.getProperty("user.home");

    /* Directory with TV shows. */
    public String tvShowDirectory = "";

    /* If this is TRUE than resolution token is omitted from replacementString if should be empty. */
    public boolean skipEmptyResolutionToken = false;

    /* This works only if skipEmptyResolutionToken is TRUE as well.
    It removes all directly connected characters except spaces and all subsequent spaces replaces with single space. */
    public boolean aggressivelySkipEmptyResolutionToken = false;

    /* How long should program wait for TheTVDB show results. */
    public int awaitTermination = 15;

    public Preferences() {
    }
}
