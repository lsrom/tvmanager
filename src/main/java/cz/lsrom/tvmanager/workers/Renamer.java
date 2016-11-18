package cz.lsrom.tvmanager.workers;

import com.sun.istack.internal.NotNull;
import cz.lsrom.tvmanager.model.Episode;
import cz.lsrom.tvmanager.model.EpisodeFile;
import cz.lsrom.tvmanager.model.ReplacementToken;
import cz.lsrom.tvmanager.model.Show;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lsrom on 11/10/16.
 */
public class Renamer {
    private static Logger logger = LoggerFactory.getLogger(Renamer.class);

    private static ConcurrentHashMap<String, String> alreadyDownloaded;
    private static ConcurrentHashMap<String, Show> shows;

    private static Pattern seasonNumber = Pattern.compile(".*(%\\d*s).*");
    private static Pattern episodeNumber = Pattern.compile(".*(%\\d*e).*");

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
            s.setEpisodes(tvdbProvider.getAllEpisodesForShow(s.getId()));

            shows.put(s.getTitle().toLowerCase(), s);
            alreadyDownloaded.put(episodeFile.getShowName().toLowerCase(), s.getId());

            logger.debug("New show {} added.", episodeFile.getShowName());
        }
    }

    public String getNewFileName (EpisodeFile episodeFile, String replacementString){
        Episode episode;
        Matcher matcher;

        if (episodeFile.getSeason() == -1){
            episode = findEpisode(episodeFile.getShowName(), episodeFile.getSeason(), -1, episodeFile.getEpisodeNum());
        } else {
            episode = findEpisode(episodeFile.getShowName(), episodeFile.getSeason(), episodeFile.getEpisodeNum(), null);
        }

        if (episode == null){return null;}

        String tmp = replacementString;

        tmp = tmp.replace(ReplacementToken.SHOW_NAME.getToken(), episodeFile.getShowName());

        matcher = seasonNumber.matcher(tmp);
        if (matcher.matches() && matcher.groupCount() == 1){
            String s = matcher.group(1).replaceAll("\\D", "");
            String newSeasonNum = String.format("%0" + s + "d", episode.getSeason());
            tmp = tmp.replaceAll(seasonNumber.pattern().replaceAll("\\.\\*", ""), newSeasonNum);
        }

        matcher = episodeNumber.matcher(tmp);
        if (matcher.matches() && matcher.groupCount() == 1){
            String s = matcher.group(1).replaceAll("\\D", "");
            String newEpisodeNum = String.format("%0" + s + "d", episode.getEpisodeNumber());
            tmp = tmp.replaceAll(episodeNumber.pattern().replaceAll("\\.\\*", ""), newEpisodeNum);
        }

        tmp = tmp.replace(ReplacementToken.EPISODE_TITLE.getToken(), episode.getTitle());

        return tmp;
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
