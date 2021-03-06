package cz.lsrom.tvmanager.workers;

import com.sun.istack.internal.NotNull;
import cz.lsrom.tvmanager.UIStarter;
import cz.lsrom.tvmanager.controller.PreferencesController;
import cz.lsrom.tvmanager.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
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
    
    private Preferences preferences;

    private TheTVDBProvider tvdbProvider;
    private BufferedWriter historyWritter;

    /**
     * Create new Renamer object. This will login to TheTVDB.
     */
    public Renamer(Preferences preferences) throws IOException {
        this.preferences = preferences;
        
        tvdbProvider = TheTVDBProvider.login();
        shows = new ConcurrentHashMap<>();
        alreadyDownloaded = new ConcurrentHashMap<>();

        if (preferences.saveRenameHistory){
            if (preferences.saveRenameHistoryToShowDir){
                // todo
            } else {
                File file = new File(preferences.customRenameHistoryLocation);

                if (file.isFile()){
                    historyWritter = new BufferedWriter(new FileWriter(preferences.customRenameHistoryLocation));
                } else {
                    historyWritter = new BufferedWriter(new FileWriter(
                            preferences.customRenameHistoryLocation.endsWith(System.getProperty("file.separator")) ?
                                    preferences.customRenameHistoryLocation + PreferencesController.RENAME_HISTORY_FILE :
                                    preferences.customRenameHistoryLocation + System.getProperty("file.separator") + PreferencesController.RENAME_HISTORY_FILE));
                }
            }
        }
    }

    /**
     * If called, flushes rename history buffer. If buffer is not set, nothing happens.
     */
    public void forceFlushHistory (){
        if (historyWritter != null){
            try {
                historyWritter.flush();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
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
        String showTitle = episodeFile.getShowName();
        int count = 0;
        while (s == null){
            s= tvdbProvider.searchForShow((showTitle = showTitle.substring(0, showTitle.lastIndexOf(" "))));
            logger.debug(showTitle);
            count++;
            if (count >= preferences.maxShowNameRetries){break;}    // so we don't loop forever
        }

        episodeFile.setShowName(showTitle);

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
        tmp = removeUnallowedCharacters(tmp);

        String ext = episodeFile.getFile().getName();   // get old filename
        ext = ext.substring(ext.lastIndexOf("."));      // and extract it's file extension from it

        return tmp + ext;   // this is new filename with file extension
    }

    /**
     * Makes the action of renaming the file. Takes the old file location from EpisodeFile and moves the file to the new location.
     * New location is old directory followed by new filename. If there is already a file with this name in the new
     * location, it is replaced.
     *
     * @param episodeFile EpisodeFile with old file path to current file and new filename.
     * @return EpisodeFile with updated file path - old file is replaced with new one with new filename.
     * @throws IOException When file can't be accessed or new location cannot be write into.
     */
    public EpisodeFile rename (EpisodeFile episodeFile) throws IOException {
        Path p = Paths.get(episodeFile.getDirectory() + System.getProperty("file.separator") + episodeFile.getNewFilename());

        if (episodeFile.getNewFilename() == null || episodeFile.getNewFilename().isEmpty()){
            return episodeFile;
        }

        Files.move(episodeFile.getFile().toPath(), p, StandardCopyOption.REPLACE_EXISTING);

        episodeFile.setFile(p.toFile());    // set filepath for the new file

        // if user wants to save rename history and new filename is different from old one
        if (preferences.saveRenameHistory && !p.toString().equals(episodeFile.getFile().toString())){
            historyWritter.write(episodeFile.getFile().toString() + RENAME_HISTORY_SEPARATOR + p.toString() + "\r\n");
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
            if (episodes.size() < absoluteEpisode){ // check for out of bounds
                String num = absoluteEpisode.toString();

                // if episode num is at least three digits long, assume it's season and ep num like this: 714 for s7e14
                if (num.length() >= 3){
                    episode = Integer.valueOf(num.substring(num.length() - 2, num.length()));   // last two digits are episode
                    season = Integer.valueOf(num.replace(episode + "", ""));    // all remaining digits are season

                    return findEpisode(showId, season, episode, null);      // search again with new parameters
                }
            } else {
                return episodes.get(absoluteEpisode - 1);
            }
        }

        return null;
    }

    /**
     * Removes characters that are not allowed in the filename.
     *
     * @param filename String with filename to be cleared from un-allowed characters.
     * @return String without un-allowed characters.
     */
    private String removeUnallowedCharacters (@NotNull String filename){
        return filename.replaceAll("[\\?!,:]", "");
    }
}
