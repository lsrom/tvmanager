package cz.lsrom.tvmanager.model;

import java.util.List;

/**
 * Created by lsrom on 11/9/16.
 */
public class Show {
    private String title;
    private List<Season> seasons;
    private String id;

    public Show(String title, List<Season> seasons, String id) {
        this.title = title;
        this.seasons = seasons;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public String getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSeasons(List<Season> seasons) {
        this.seasons = seasons;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Show show = (Show) o;

        if (!title.equals(show.title)) return false;
        return id.equals(show.id);

    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Show{");
        sb.append("title='").append(title).append('\'');
        sb.append(", seasons=").append(seasons);
        sb.append(", id='").append(id).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
