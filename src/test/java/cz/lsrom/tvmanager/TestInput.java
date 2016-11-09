package cz.lsrom.tvmanager;

import java.io.File;

/**
 * Created by lsrom on 11/9/16.
 */
public class TestInput {
    public final File input;
    public final String showName;
    public final Integer season;
    public final int episodeNum;
    public final String episodeTitle;
    public final String resolution;
    public final String directory;

    public TestInput(File input, String showName, Integer season, int episodeNum, String episodeTitle, String resolution, String directory) {
        this.input = input;
        this.showName = showName;
        this.season = season;
        this.episodeNum = episodeNum;
        this.episodeTitle = episodeTitle;
        this.resolution = resolution;
        this.directory = directory;
    }
}
