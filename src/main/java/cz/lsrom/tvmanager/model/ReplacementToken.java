package cz.lsrom.tvmanager.model;

/**
 * Created by lsrom on 11/17/16.
 */
public enum ReplacementToken {
    SHOW_NAME("%S", "Name of the show."),
    SEASON_NUM("%2s", "Season number."),
    EPISODE_NUM("%2e", "Episode number."),
    EPISODE_ABS_NUM("%3E", "Absolute episode number."),
    EPISODE_TITLE("%t", "Episode title."),
    EPISODE_RESOLUTION("%r", "Episode resolution.");

    private final String token;
    private final String tokenDescription;

    ReplacementToken(String token, String tokenDescription) {
        this.token = token;
        this.tokenDescription = tokenDescription;
    }

    public String getToken() {
        return token;
    }

    public String getTokenDescription() {
        return tokenDescription;
    }

    @Override
    public String toString() {
        return token;
    }
}
