package cz.lsrom.tvmanager.model;

import java.util.Date;

/**
 * Created by lsrom on 11/9/16.
 */
public class Episode {
    private String title;
    private int episodeNumber;
    private int absoluteEpisodeNumber;
    private String resolution;
    private String overview;
    private Date airDate;

    public Episode(String title, int episodeNumber, int absoluteEpisodeNumber, String resolution, String overview, Date airDate) {
        this.title = title;
        this.episodeNumber = episodeNumber;
        this.absoluteEpisodeNumber = absoluteEpisodeNumber;
        this.resolution = resolution;
        this.overview = overview;
        this.airDate = airDate;
    }

    public String getTitle() {
        return title;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public int getAbsoluteEpisodeNumber() {
        return absoluteEpisodeNumber;
    }

    public String getResolution() {
        return resolution;
    }

    public String getOverview() {
        return overview;
    }

    public Date getAirDate() {
        return airDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEpisodeNumber(int episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public void setAbsoluteEpisodeNumber(int absoluteEpisodeNumber) {
        this.absoluteEpisodeNumber = absoluteEpisodeNumber;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setAirDate(Date airDate) {
        this.airDate = airDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Episode episode = (Episode) o;

        if (episodeNumber != episode.episodeNumber) return false;
        if (absoluteEpisodeNumber != episode.absoluteEpisodeNumber) return false;
        if (!title.equals(episode.title)) return false;
        return airDate.equals(episode.airDate);

    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + episodeNumber;
        result = 31 * result + absoluteEpisodeNumber;
        result = 31 * result + airDate.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Episode{");
        sb.append("title='").append(title).append('\'');
        sb.append(", episodeNumber=").append(episodeNumber);
        sb.append(", absoluteEpisodeNumber=").append(absoluteEpisodeNumber);
        sb.append(", resolution='").append(resolution).append('\'');
        sb.append(", overview='").append(overview).append('\'');
        sb.append(", airDate=").append(airDate);
        sb.append('}');
        return sb.toString();
    }
}
