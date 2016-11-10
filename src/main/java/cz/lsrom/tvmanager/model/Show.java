package cz.lsrom.tvmanager.model;

import java.util.List;

/**
 * Created by lsrom on 11/9/16.
 */
public class Show {
    private String title;
    private List<Episode> episodes;
    private String id;
    private String overview;
    private String status;

    public Show(String title, List<Episode> episodes, String id, String overview, String status) {
        this.title = title;
        this.episodes = episodes;
        this.id = id;
        this.overview = overview;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public String getId() {
        return id;
    }

    public String getOverview() {
        return overview;
    }

    public String getStatus() {
        return status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Show show = (Show) o;

        if (!title.equals(show.title)) return false;
        if (!id.equals(show.id)) return false;
        return status.equals(show.status);

    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + status.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Show{");
        sb.append("title='").append(title).append('\'');
        sb.append(", episodes=").append(episodes);
        sb.append(", id='").append(id).append('\'');
        sb.append(", overview='").append(overview).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
