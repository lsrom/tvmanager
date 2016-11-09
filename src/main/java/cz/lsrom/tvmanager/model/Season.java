package cz.lsrom.tvmanager.model;

import java.util.List;

/**
 * Created by lsrom on 11/9/16.
 */
public class Season {
    private int seasonNumber;
    private List<Episode> episodes;

    public Season(int seasonNumber, List<Episode> episodes) {
        this.seasonNumber = seasonNumber;
        this.episodes = episodes;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Season season = (Season) o;

        if (seasonNumber != season.seasonNumber) return false;
        return episodes.equals(season.episodes);

    }

    @Override
    public int hashCode() {
        int result = seasonNumber;
        result = 31 * result + episodes.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Season{");
        sb.append("seasonNumber=").append(seasonNumber);
        sb.append(", episodes=").append(episodes);
        sb.append('}');
        return sb.toString();
    }
}
