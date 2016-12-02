package cz.lsrom.tvmanager.workers;

import com.sun.istack.internal.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lsrom on 12/2/16.
 */
public abstract class FileMover {
    private static Logger logger = LoggerFactory.getLogger(FileMover.class);

    private static Pattern seasonNumber = Pattern.compile(".*(%\\d*s).*");

    /**
     * Moves single file given by it's path to new path. If any directories in the path are missing, this method will
     * create them.
     *
     * @param originPath Path from which the file is to be moved.
     * @param targetPath Path to which the file is to be moved.
     */
    public static void moveFile (@NotNull final String originPath, @NotNull final String targetPath){
        try {
            Files.move(Paths.get(originPath), Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Creates path for TV show episode to be copied. This path depends upon the seasonFormat parameter.
     * If this parameter is set and contains season format string (for example %2s) this format string is replaced with
     * season number in proper format (in this case season number padded to two places). If there is no format string,
     * the seasonFormat will be simply used as directory for episodes.
     * If no seasonFormat is specified, all episodes are put directly into the TV show directory.
     *
     * @param showDirPath Path to the directory for this TV show.
     * @param seasonFormat Format string for creating season directory.
     * @param season Which season is this.
     * @param fileName Name of the file which is to be put in the directory.
     * @return Path to the file with given directory, season directory and filename.
     */
    public static String getTargetPath (@NotNull String showDirPath, @NotNull String seasonFormat, final int season, @NotNull final String fileName){
        // check for file separator at the end of directory, if it's missing, add it
        if (!showDirPath.endsWith(System.getProperty("file.separator"))){
            showDirPath += System.getProperty("file.separator");
        }

        // if seasonFormat is empty, we can return dir + filename
        if (seasonFormat.isEmpty()){
            return showDirPath + fileName;
        }

        Matcher matcher;

        matcher = seasonNumber.matcher(seasonFormat);    // check if there is token for season number
        if (matcher.matches() && matcher.groupCount() == 1){
            String s = matcher.group(1).replaceAll("\\D", "");  // get only number from the token - it says how many zeroes to prefix
            String newSeasonNum = String.format("%0" + s + "d", season);   // format season number
            seasonFormat = seasonFormat.replaceAll(seasonNumber.pattern().replaceAll("\\.\\*", ""), newSeasonNum);    // set season number
        }

        // return formatted path with season dir
        return showDirPath + seasonFormat + System.getProperty("file.separator") + fileName;
    }
}
