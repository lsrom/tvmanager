package cz.lsrom.tvmanager.model;

/**
 * Created by lsrom on 11/17/16.
 */
public class Preferences {
    private String replacementString = ReplacementToken.SHOW_NAME + " s" + ReplacementToken.SEASON_NUM + "e" + ReplacementToken.EPISODE_NUM + " " + ReplacementToken.EPISODE_TITLE;

    public Preferences() {
    }

    public String getReplacementString() {
        return replacementString;
    }

    public void setReplacementString(String replacementString) {
        this.replacementString = replacementString;
    }
}
