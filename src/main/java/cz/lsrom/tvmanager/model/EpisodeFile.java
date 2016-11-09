package cz.lsrom.tvmanager.model;

import java.io.File;

/**
 * Created by lsrom on 11/9/16.
 */
public class EpisodeFile {
    private String showName;
    private Integer season;
    private int episodeNum;
    private String resolution;
    private File file;

    public EpisodeFile(String showName, Integer season, int episodeNum, String resolution, File file) {
        this.showName = showName;
        this.season = season;
        this.episodeNum = episodeNum;
        this.resolution = resolution;
        this.file = file;
    }

    public String getShowName() {
        return showName;
    }

    public Integer getSeason() {
        return season;
    }

    public int getEpisodeNum() {
        return episodeNum;
    }

    public String getResolution() {
        return resolution;
    }

    public File getFile() {
        return file;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public void setSeason(Integer season) {
        this.season = season;
    }

    public void setEpisodeNum(int episodeNum) {
        this.episodeNum = episodeNum;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EpisodeFile that = (EpisodeFile) o;

        if (episodeNum != that.episodeNum) return false;
        if (!showName.equals(that.showName)) return false;
        if (season != null ? !season.equals(that.season) : that.season != null) return false;
        return file.equals(that.file);

    }

    @Override
    public int hashCode() {
        int result = showName.hashCode();
        result = 31 * result + (season != null ? season.hashCode() : 0);
        result = 31 * result + episodeNum;
        result = 31 * result + file.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EpisodeFile{");
        sb.append("showName='").append(showName).append('\'');
        sb.append(", season=").append(season);
        sb.append(", episodeNum=").append(episodeNum);
        sb.append(", resolution='").append(resolution).append('\'');
        sb.append(", file=").append(file);
        sb.append('}');
        return sb.toString();
    }
}
