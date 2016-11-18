package cz.lsrom.tvmanager.model;

/**
 * Created by lsrom on 11/17/16.
 */
public class Preferences {
    public String replacementString = ReplacementToken.SHOW_NAME + " s" + ReplacementToken.SEASON_NUM + "e" + ReplacementToken.EPISODE_NUM + " " + ReplacementToken.EPISODE_TITLE;
    public String defaultFileChooserOpenLocation = System.getProperty("user.home");

    public Preferences() {
    }
}