package cz.lsrom.tvmanager.workers;

import com.sun.istack.internal.NotNull;
import cz.lsrom.tvmanager.model.EpisodeFile;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lsrom on 11/9/16.
 */
public abstract class Parser {
    // regular expression to try on given filename
    private static final String[] REGEX = {
            "(.+)[\\D\\Wa\\\\b](\\d\\d\\d)[\\D\\Wa\\\\b].*",     // matches anime files (2 groups = show, episode)
            "(.+?\\W\\D*?)[sS](\\d\\d?)[eE](\\d\\d?).*",    // matches normal shows (3 groups = show, season, episode)
            "(.+?)\\d{4}.*[sS](\\d\\d?)[eE](\\d\\d?).*",      // matches normal shows with year (3 groups = show, season, episode)
            "(.+\\W\\D*?)[sS](\\d\\d?)\\D*?[eE](\\d\\d?).*",    // matches normal show (3 groups = show, season, episode)
    };

    // regular expression to match resolution part of filename
    private static final String RESOLUTION_REGEX = ".*\\D(\\d+[pk]).*";

    // compiled regular expressions
    private static final Pattern[] COMPILED_REGEX = new Pattern[REGEX.length];
    private static final Pattern COMPILED_RESOLUTION_REGEX = Pattern.compile(RESOLUTION_REGEX);

    // compile regular expressions
    static {
        for (int i = 0; i < REGEX.length; i++){
            COMPILED_REGEX[i] = Pattern.compile(REGEX[i]);
        }
    }

    public static EpisodeFile parse (File episodeFile){
        String fileName = stripJunk(episodeFile.getName());     // strip junk chars and strings from filename

        String directory = episodeFile.getParent() == null ? "" : episodeFile.getParent();
        String showTitle = "";
        String showId = "";
        int season = -1;
        int episode = -1;
        String resolution = "";

        Matcher matcher = null;

        // first try to match resolution string and remove it for simpler parsing later
        matcher = COMPILED_RESOLUTION_REGEX.matcher(fileName);
        if (matcher.matches() && matcher.groupCount() == 1){
            resolution = matcher.group(1);
            fileName = fileName.replace(resolution, "");        // remove resolution from filename
            fileName = stripJunk(fileName);                     // removing resolution may leave junk in the filename
        }

        int lastMatchCount = 0;     // for checking of current match is better than the one already used

        for (Pattern p : COMPILED_REGEX){
            matcher = p.matcher(fileName);

            if (matcher.matches()){
                switch (matcher.groupCount()){
                    case 2:
                        if (lastMatchCount < 2){    // only set new values if this is better match than the last one
                            showTitle = stripJunkSuffix(matcher.group(1));            // show title might have junk characters at the end
                            episode = Integer.valueOf(matcher.group(2));

                            lastMatchCount = 2;     // set to 2 as this is the current level of match (group count)
                        }
                        break;
                    case 3:
                        if (lastMatchCount < 3){    // only set new values if this is better match than the last one
                            showTitle = stripJunkSuffix(matcher.group(1));
                            season = Integer.valueOf(matcher.group(2));
                            episode = Integer.valueOf(matcher.group(3));

                            lastMatchCount = 3;     // set to 3 as this is the current level of match (group count)

                            // if we have group count 3 we want to end the loop because we have the best result
                            return new EpisodeFile(capitalizeString(showTitle), showId, season, episode, resolution, directory, episodeFile);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        // return EpisodeFile with the values we've got
        return new EpisodeFile(capitalizeString(showTitle), showId, season, episode, resolution, directory, episodeFile);
    }

    /**
     * Remove any characters and substring that are considered junk.
     * As junk is considered multiple occurrences of space, dash or underscore. Resulting string is guaranteed
     * to have only single spaces, underscores or dashes.
     *
     * Any substring enclosed in [] on the beginning of the string is removed and if filename ends with substring
     * enclosed in [] which doesn't contain resolution string, this substring is removed as well.
     *
     * When parameter is null or empty, empty string is returned.
     *
     * @param input String to be stripped of junk characters and substrings.
     * @return String without junk characters and substring or empty string.
     */
    private static String stripJunk (final String input){
        if (input == null || input.isEmpty()){ return ""; }     // if string is null or empty return empty string

        String output = input;

        // remove any substring at the beginning enclosed in [] as it would mess up regex matching
        if (output.startsWith("[")){
            int x = output.indexOf("]");
            output = output.substring(x + 1, output.length());
        }

        // remove any substring at the end enclosed in [] as it would mess up regex matching
        // but only if it doesn't contain resolution string
        if (output.matches("^.*([\\]]\\.[0-9a-z]{3,4})$") && !output.matches("^.*(\\d+[pk])[\\]]\\.[a-z]{3,4}$")){
            int x = output.lastIndexOf("[");
            String ext = output.substring(output.lastIndexOf("."), output.length());
            output = output.substring(0, x) + ext;
        }

        // if there is more than one occurrence of '-' remove it all, allow one as it might be part of the title
        if (hasMultipleOccurrences(output, '-')){
            output = output.replaceAll("-", " ");
        }

        // if there is more than one occurrence of '_' remove it all, allow one as it might be part of the title
        if (hasMultipleOccurrences(output, '_')){
            output = output.replaceAll("_", " ");
        }

        // remove junk substrings
        output = output.replace("x264", "");
        output = output.replace("X264", "");
        output = output.replace("x265", "");
        output = output.replace("X265", "");

        // remove empty brackets
        output = output.replaceAll("\\[\\]", "");

        output = output.replaceAll("_", " ");           // remove all '_' from output
        output = output.replaceAll("[ ]{2,}", " ");     // replace any subsequent spaces with single space
        output = output.replaceAll("[\\.]{1,}", " ");   // replace any dots with single space
        output = output.replaceAll("$[-]", "");         // remove leading underscores

        return output.trim();
    }

    /**
     * Removes junk characters from the end of the string. Junk characters are any non-word characters as considered
     * by regex engine and underscore.
     *
     * When parameter is null or empty, empty string is returned.
     *
     * @param input String to be stripped of junk characters at the end.
     * @return String without junk characters as suffix.
     */
    private static String stripJunkSuffix (final String input){
        if (input == null || input.isEmpty()){ return ""; }     // if string is null or empty return empty string

        String output = input;

        // remove ending substring if it is not a word character
        if (output.matches(".*[\\W_]+$")){
            output = output.replaceAll("[\\W_]+$", "");
        }

        return output;
    }

    /**
     * Checks if given character is present in the string more than once.
     * Note that it doesn't count the number of occurrences, only checks if there is more then one.
     * If null is passed or parameter string is empty, returns FALSE.
     *
     * @param str String to be searched for occurrences of given character.
     * @param c Character to be count in the string.
     * @return Boolean TRUE if character is in the string multiple times, otherwise FALSE.
     */
    private static boolean hasMultipleOccurrences (@NotNull String str, char c){
        if (str == null || str.isEmpty()){return false;}
        if (str.indexOf(c) != str.lastIndexOf(c)){
            return true;
        }

        return false;
    }

    /**
     * Takes string as input and returns the same string but with every starting letter of a word capitalized.
     * Words are separated by spaces. If parameter 'str' is null or empty, returns empty string.
     *
     * @param str String to be capitalized.
     * @return Capitalized string.
     */
    private static String capitalizeString (@NotNull String str){
        if (str == null || str.isEmpty()){return "";}
        String[] array = str.split(" ");
        StringBuilder stringBuilder = new StringBuilder();

        for (String s : array){
            stringBuilder.append(s.substring(0, 1).toUpperCase());
            stringBuilder.append(s.substring(1, s.length()));
            stringBuilder.append(" ");
        }

        return stringBuilder.toString().trim();
    }
}
