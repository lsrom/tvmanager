package cz.lsrom.tvmanager.workers;

import com.sun.istack.internal.NotNull;
import cz.lsrom.tvmanager.UIStarter;
import cz.lsrom.tvmanager.controller.PreferencesController;
import cz.lsrom.tvmanager.model.Episode;
import cz.lsrom.tvmanager.model.EpisodeFile;
import cz.lsrom.tvmanager.model.ReplacementToken;
import cz.lsrom.tvmanager.model.Show;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lsrom on 11/10/16.
 */
public class Renamer {
    private static Logger logger = LoggerFactory.getLogger(Renamer.class);

    private static final String RENAME_HISTORY_SEPARATOR = " -> ";

    private static ConcurrentHashMap<String, String> alreadyDownloaded;
    private static ConcurrentHashMap<String, Show> shows;

    private static Pattern seasonNumber = Pattern.compile(".*(%\\d*s).*");
    private static Pattern episodeNumber = Pattern.compile(".*(%\\d*e).*");
    private static Pattern episodeNumberAbs = Pattern.compile(".*(%\\d*E).*");
    private static Pattern episodeResolution = Pattern.compile(".*(%r).*");

    private TheTVDBProvider tvdbProvider;

    private BufferedWriter historyWritter;

    /**
     * Create new Renamer object. This will login to TheTVDB.
     */
    public Renamer() throws IOException {
        tvdbProvider = TheTVDBProvider.login();
        shows = new ConcurrentHashMap<>();
        alreadyDownloaded = new ConcurrentHashMap<>();

        if (UIStarter.preferences.saveRenameHistory){
            if (UIStarter.preferences.saveRenameHistoryToShowDir){
                // todo
            } else {
                File file = new File(UIStarter.preferences.customRenameHistoryLocation);

                if (file.isFile()){
                    historyWritter = new BufferedWriter(new FileWriter(UIStarter.preferences.customRenameHistoryLocation));
                } else {
                    historyWritter = new BufferedWriter(new FileWriter(
                            UIStarter.preferences.customRenameHistoryLocation.endsWith(System.getProperty("file.separator")) ?
                                    UIStarter.preferences.customRenameHistoryLocation + PreferencesController.RENAME_HISTORY_FILE :
                                    UIStarter.preferences.customRenameHistoryLocation + System.getProperty("file.separator") + PreferencesController.RENAME_HISTORY_FILE));
                }
            }
        }
    }

    public void forceFlushHistory (){
        try {
            historyWritter.flush();
            historyWritter.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * If show metadata are not yet downloaded from TheTVDB this method will download them and store them.
     * It is safe (and in fact encouraged) to run this method in multiple threads as shows are stored in ConcurrentHashMap.
     *
     * @param episodeFile Episode of show we want to download.
     */
    public void addShow (@NotNull EpisodeFile episodeFile){
        if (alreadyDownloaded.containsKey(episodeFile.getShowName().toLowerCase())){
            episodeFile.setShowId(alreadyDownloaded.get(episodeFile.getShowName().toLowerCase()));
            return;     // if we already have the show, don't add it again
        }

        logger.debug("Adding new show {}.", episodeFile.getShowName());

        Show s = tvdbProvider.searchForShow(episodeFile.getShowName());     // search for the show with it's name parsed from the file
        s.setEpisodes(tvdbProvider.getAllEpisodesForShow(s.getId()));       // now get all episodes for the show
        episodeFile.setShowId(s.getId());

        shows.put(s.getId().toLowerCase(), s);       // save show with episodes
        alreadyDownloaded.put(episodeFile.getShowName().toLowerCase(), s.getId());  // save show name to already downloaded so we have easy way of checking if we have the show or not
    }

    /**
     * Creates new filename based on downloaded metadata and replacement string structure. Returns either string with
     * new filename or null if no metadata for this episode was found.
     *
     * @param episodeFile Episode for which new filename should be found.
     * @param replacementString String with structure of the new filename.
     * @return String with new filename or null.
     */
    public String getNewFileName (EpisodeFile episodeFile, String replacementString){
        Episode episode;
        Matcher matcher;

        if (episodeFile.getSeason() == -1){     // if season is -1 then search for episode based on it's absolute number
            episode = findEpisode(episodeFile.getShowId(), episodeFile.getSeason(), -1, episodeFile.getEpisodeNum());
        } else {        // search for episode based on it's season and episode number
            episode = findEpisode(episodeFile.getShowId(), episodeFile.getSeason(), episodeFile.getEpisodeNum(), null);
        }

        if (episode == null){return null;}      // we didn't find any episode, return null

        String tmp = replacementString;     // this string will be transformed to new filename

        tmp = tmp.replace(ReplacementToken.SHOW_NAME.getToken(), episodeFile.getShowName());    // set show name

        matcher = seasonNumber.matcher(tmp);    // check if there is token for season number
        if (matcher.matches() && matcher.groupCount() == 1){
            String s = matcher.group(1).replaceAll("\\D", "");  // get only number from the token - it says how many zeroes to prefix
            String newSeasonNum = String.format("%0" + s + "d", episode.getSeason());   // format season number
            tmp = tmp.replaceAll(seasonNumber.pattern().replaceAll("\\.\\*", ""), newSeasonNum);    // set season number
        }

        matcher = episodeNumber.matcher(tmp);   // check if there is token for episode number
        if (matcher.matches() && matcher.groupCount() == 1){
            String s = matcher.group(1).replaceAll("\\D", "");  // get only number from the token - it says how many zeroes to prefix
            String newEpisodeNum = String.format("%0" + s + "d", episode.getEpisodeNumber());   // format episode number
            tmp = tmp.replaceAll(episodeNumber.pattern().replaceAll("\\.\\*", ""), newEpisodeNum);  // set episode number
        }

        matcher = episodeNumberAbs.matcher(tmp);    // check if there is token for absolute episode number
        if (matcher.matches() && matcher.groupCount() == 1){
            String s = matcher.group(1).replaceAll("\\D", "");  // get only number from the token - it says how many zeroes to prefix
            String newEpisodeNum = String.format("%0" + s + "d", episode.getAbsoluteEpisodeNumber());   // format absolute episode number
            tmp = tmp.replaceAll(episodeNumberAbs.pattern().replaceAll("\\.\\*", ""), newEpisodeNum);   // set absolute episode number
        }

        matcher = episodeResolution.matcher(tmp);   // check if there is token for episode resolution
        if (matcher.matches() && matcher.groupCount() == 1){
            tmp = tmp.replaceAll(episodeResolution.pattern().replaceAll("\\.\\*", ""), episodeFile.getResolution());
        }

        tmp = tmp.replace(ReplacementToken.EPISODE_TITLE.getToken(), episode.getTitle());   // set episode title

        String ext = episodeFile.getFile().getName();   // get old filename
        ext = ext.substring(ext.lastIndexOf("."));      // and extract it's file extension from it

        return tmp + ext;   // this is new filename with file extension
    }

    /**
     * Makes the action of renaming the file. Takes the old file location from EpisodeFile and moves the file to the new location.
     * New location is old directory followed by new filename.
     *
     * @param episodeFile EpisodeFile with old file path to current file and new filename.
     * @return EpisodeFile with updated file path - old file is replaced with new one with new filename.
     * @throws IOException When file can't be accessed or new location cannot be write into.
     */
    public EpisodeFile rename (EpisodeFile episodeFile) throws IOException {
        Path p = Paths.get(episodeFile.getDirectory() + System.getProperty("file.separator") + episodeFile.getNewFilename());
        Files.move(episodeFile.getFile().toPath(), p);

        episodeFile.setFile(p.toFile());    // set filepath for the new file

        if (UIStarter.preferences.saveRenameHistory){   // if user wants to save rename history
            historyWritter.write(episodeFile.getFile().toString() + RENAME_HISTORY_SEPARATOR + p.toString());
        }

        return episodeFile;     // return episode with new filepath
    }

    /**
     * Find episode in episode list. can search either based on absolute episode number or combination of season number
     * and episode number.
     *
     * @param showId Id of the show this episode belongs to.
     * @param season What season is this episode in? If absoluteEpisode parameter is set, this can be whatever.
     * @param episode What episode in given season is this? If absoluteEpisode parameter is set, this can be whatever.
     * @param absoluteEpisode If this is set, search uses absolute number of episode to find it.
     * @return New Episode object.
     */
    private Episode findEpisode (String showId, int season, int episode, Integer absoluteEpisode){
        List<Episode> episodes = shows.get(showId.toLowerCase()).getEpisodes();   // get episodes for show based on shows name

        if (absoluteEpisode == null || absoluteEpisode == -1){   // search using season and episode number
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
