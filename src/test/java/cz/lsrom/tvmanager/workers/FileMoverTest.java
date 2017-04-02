package cz.lsrom.tvmanager.workers;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by lsrom on 12/2/16.
 */
public class FileMoverTest {

    @Test
    public void testGetTargetPathNoFileSeparatorInShowDir (){
        String expected = File.separator + "path" + File.separator + "to" + File.separator + "show" + File.separator + "dir" + File.separator + "Season 02" + File.separator + "cool_show.mkv";
        String path = FileMover.getTargetPath(File.separator + "path" + File.separator + "to" + File.separator + "show" + File.separator + "dir", "Season %2s", 2, "cool_show.mkv");

        assertEquals(expected, path);
    }

    @Test
    public void testGetTargetPathFileSeparatorInShowDir (){
        String expected = File.separator + "path" + File.separator + "to" + File.separator + "show" + File.separator + "dir" + File.separator + "Season 02" + File.separator + "cool_show.mkv";
        String path = FileMover.getTargetPath(File.separator + "path" + File.separator + "to" + File.separator + "show" + File.separator + "dir" + File.separator + "", "Season %2s", 2, "cool_show.mkv");

        assertEquals(expected, path);
    }

    @Test
    public void testGetTargetPathSeasonPaddingThree (){
        String expected = File.separator + "path" + File.separator + "to" + File.separator + "show" + File.separator + "dir" + File.separator + "Season 002" + File.separator + "cool_show.mkv";
        String path = FileMover.getTargetPath(File.separator + "path" + File.separator + "to" + File.separator + "show" + File.separator + "dir", "Season %3s", 2, "cool_show.mkv");

        assertEquals(expected, path);
    }

    @Test
    public void testGetTargetPathSeasonPaddingOne (){
        String expected = File.separator + "path" + File.separator + "to" + File.separator + "show" + File.separator + "dir" + File.separator + "Season 2" + File.separator + "cool_show.mkv";
        String path = FileMover.getTargetPath(File.separator + "path" + File.separator + "to" + File.separator + "show" + File.separator + "dir", "Season %1s", 2, "cool_show.mkv");

        assertEquals(expected, path);
    }

    @Test
    public void testGetTargetPathSeasonPaddingOneLongSeasonNumber (){
        String expected = File.separator + "path" + File.separator + "to" + File.separator + "show" + File.separator + "dir" + File.separator + "Season 22" + File.separator + "cool_show.mkv";
        String path = FileMover.getTargetPath(File.separator + "path" + File.separator + "to" + File.separator + "show" + File.separator + "dir", "Season %1s", 22, "cool_show.mkv");

        assertEquals(expected, path);
    }

    @Test
    public void testGetTargetPathEmptySeasonFormat (){
        String expected = File.separator + "path" + File.separator + "to" + File.separator + "show" + File.separator + "dir" + File.separator + "cool_show.mkv";
        String path = FileMover.getTargetPath(File.separator + "path" + File.separator + "to" + File.separator + "show" + File.separator + "dir", "", 2, "cool_show.mkv");

        assertEquals(expected, path);
    }

    @Test
    public void testGetTargetPathSeasonFormatWithoutFormatString (){
        String expected = File.separator + "path" + File.separator + "to" + File.separator + "show" + File.separator + "dir" + File.separator + "Season" + File.separator + "cool_show.mkv";
        String path = FileMover.getTargetPath(File.separator + "path" + File.separator + "to" + File.separator + "show" + File.separator + "dir", "Season", 2, "cool_show.mkv");

        assertEquals(expected, path);
    }
}
