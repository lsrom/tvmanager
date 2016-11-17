package cz.lsrom.tvmanager.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by lsrom on 11/9/16.
 */
public class Episode implements Comparable {
    private String title;
    private int episodeNumber;
    private int dvdEpisodeNumber;
    private int absoluteEpisodeNumber;
    private int season;
    private int dvdSeason;
    private String overview;
    private Date airDate;
    private int episodeId;

    public Episode(String title, int episodeNumber, int dvdEpisodeNumber, int absoluteEpisodeNumber, int season, int dvdSeason, String overview, Date airDate, int episodeId) {
        this.title = title;
        this.episodeNumber = episodeNumber;
        this.dvdEpisodeNumber = dvdEpisodeNumber;
        this.absoluteEpisodeNumber = absoluteEpisodeNumber;
        this.season = season;
        this.dvdSeason = dvdSeason;
        this.overview = overview;
        this.airDate = airDate;
        this.episodeId = episodeId;
    }

    public String getTitle() {
        return title;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public int getDvdEpisodeNumber() {
        return dvdEpisodeNumber;
    }

    public int getAbsoluteEpisodeNumber() {
        return absoluteEpisodeNumber;
    }

    public int getSeason() {
        return season;
    }

    public int getDvdSeason() {
        return dvdSeason;
    }

    public String getOverview() {
        return overview;
    }

    public Date getAirDate() {
        return airDate;
    }

    public int getEpisodeId() {
        return episodeId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEpisodeNumber(int episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public void setDvdEpisodeNumber(int dvdEpisodeNumber) {
        this.dvdEpisodeNumber = dvdEpisodeNumber;
    }

    public void setAbsoluteEpisodeNumber(int absoluteEpisodeNumber) {
        this.absoluteEpisodeNumber = absoluteEpisodeNumber;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public void setDvdSeason(int dvdSeason) {
        this.dvdSeason = dvdSeason;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setAirDate(Date airDate) {
        this.airDate = airDate;
    }

    public void setEpisodeId(int episodeId) {
        this.episodeId = episodeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Episode episode = (Episode) o;

        if (episodeNumber != episode.episodeNumber) return false;
        return season == episode.season;

    }

    @Override
    public int hashCode() {
        int result = episodeNumber;
        result = 31 * result + season;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Episode{");
        sb.append("title='").append(title).append('\'');
        sb.append(", episodeNumber=").append(episodeNumber);
        sb.append(", absoluteEpisodeNumber=").append(absoluteEpisodeNumber);
        sb.append(", season=").append(season);
        sb.append(", overview='").append(overview).append('\'');
        sb.append(", airDate=").append(airDate);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(Object o) {
        if (this.absoluteEpisodeNumber > ((Episode)o).getAbsoluteEpisodeNumber()){
            return 1;
        } else if (this.absoluteEpisodeNumber < ((Episode)o).getAbsoluteEpisodeNumber()){
            return -1;
        }

        return 0;
    }
}
