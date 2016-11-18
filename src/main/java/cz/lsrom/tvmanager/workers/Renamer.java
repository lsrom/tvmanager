package cz.lsrom.tvmanager.workers;

import com.sun.istack.internal.NotNull;
import cz.lsrom.tvmanager.model.Episode;
import cz.lsrom.tvmanager.model.EpisodeFile;
import cz.lsrom.tvmanager.model.Show;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lsrom on 11/10/16.
 */
public class Renamer {
    private static Logger logger = LoggerFactory.getLogger(Renamer.class);

    private static ConcurrentHashMap<String, String> alreadyDownloaded;
    private static ConcurrentHashMap<String, Show> shows;

    TheTVDBProvider tvdbProvider;

    public Renamer() {
        tvdbProvider = TheTVDBProvider.login();
        shows = new ConcurrentHashMap<>();
        alreadyDownloaded = new ConcurrentHashMap<>();
    }

    public void addShow (@NotNull EpisodeFile episodeFile){
        if (alreadyDownloaded.containsKey(episodeFile.getShowName().toLowerCase())){
            logger.debug("Adding already downloaded show {}.", episodeFile.getShowName());
            // todo
        } else {
            logger.debug("Adding new show {}.", episodeFile.getShowName());

            Show s = tvdbProvider.searchForShow(episodeFile.getShowName());
            alreadyDownloaded.put(episodeFile.getShowName().toLowerCase(), s.getId());
            s.setEpisodes(tvdbProvider.getAllEpisodesForShow(alreadyDownloaded.get(episodeFile.getShowName().toLowerCase())));

            shows.put(s.getTitle().toLowerCase(), s);
            logger.debug("New show {} added.", episodeFile.getShowName());
        }
    }

    public String getNewFileName (EpisodeFile episodeFile, String replacementString){
        if (episodeFile.getEpisodeNum() == -1){
            findEpisode(episodeFile.getShowName(), episodeFile.getSeason(), -1, episodeFile.getEpisodeNum());
        } else {
            findEpisode(episodeFile.getShowName(), episodeFile.getSeason(), episodeFile.getEpisodeNum(), null);
        }

        // todo make the replacement for new filename

        return "New super cool name";
    }

    private Episode findEpisode (String showName, int season, int episode, Integer absoluteEpisode){
        List<Episode> episodes = shows.get(showName.toLowerCase()).getEpisodes();

        if (absoluteEpisode == null){   // search using season and episode number
            int pos = 0;
            if ((pos = episodes.indexOf(new Episode("", episode, -1,  -1, season, -1, "", null, -1))) != -1){
                return episodes.get(pos);
            } else {
                return null;
            }

        } else {    // search using absolute number - for example with anime shows
            return episodes.get(absoluteEpisode - 1);
        }
    }
}
