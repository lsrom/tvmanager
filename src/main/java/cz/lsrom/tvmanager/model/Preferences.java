package cz.lsrom.tvmanager.model;

/**
 * Created by lsrom on 11/17/16.
 */
public class Preferences {
    /* Path to directory with tV Manager preferences file. */
    public String tvManagerPreferencesDirectory = System.getProperty("user.home") + "/.tvmanager/";

    /* Structure of the new filename. Each token is replaced by it's proper value from TheTVDB. */
    public String replacementString = ReplacementToken.SHOW_NAME + " s" + ReplacementToken.SEASON_NUM + "e" + ReplacementToken.EPISODE_NUM + " " + ReplacementToken.EPISODE_TITLE;

    /* Default location where should FileChooser open. */
    public String defaultFileChooserOpenLocation = System.getProperty("user.home");

    /* Directory with TV shows. */
    public String tvShowDirectory = "";

    /* Director where new TV episodes are downloaded into. */
    public String tvShowDownloadDirectory = "";

    /* If TRUE then on every startup files from download directory will be loaded. */
    public boolean preloadFromDownloadDirectory = false;

    /* Only files with this extension will be loaded. */
    public String[] supportedFileExtensions = {"avi", "mkv", "mp4"};

    /* Files containing these substrings will not be loaded. Case insensitive - "sample" == "Sample" == "SaMpLe" == TRUE */
    public String[] skipFilesContaining = {"sample"};

    /* If this is TRUE than resolution token is omitted from replacementString if should be empty. */
    public boolean skipEmptyResolutionToken = false;

    /* This works only if skipEmptyResolutionToken is TRUE as well.
    It removes all directly connected characters except spaces and all subsequent spaces replaces with single space. */
    public boolean aggressivelySkipEmptyResolutionToken = false;

    /* If TRUE, successfully renamed files will be moved to tvShowDirectory, in show subdirectory and to separate
     * season directories, if they are set. */
    public boolean moveAfterRename = false;

    public String seasonFormat = "Season %2s";

    /* If TRUE, then all successfully renamed files will be removed from the view. */
    public boolean removeRenamedFiles = false;

    /* If TRUE then rename history is saved. Renamed history is what file was renamed to what name. */
    public boolean saveRenameHistory = false;

    /* If TRUE then rename history is saved for each show separately in shows directory. */
    public boolean saveRenameHistoryToShowDir = false;

    /* If 'saveRenameHistoryToShowDir' is FALSE and 'saveRenameHistory' TRUE then this is the location where rename history is saved. */
    public String customRenameHistoryLocation = tvManagerPreferencesDirectory;

    /* How long should program wait for TheTVDB show results. */
    public int awaitTermination = 15;

    /* How many times to try new variants of show name. */
    public int maxShowNameRetries = 3;

    public Preferences() {
    }
}
