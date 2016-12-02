package cz.lsrom.tvmanager.workers;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by lsrom on 12/2/16.
 */
public class FileMoverTest {

    @Test
    public void testGetTargetPathNoFileSeparatorInShowDir (){
        String expected = "/path/to/show/dir/Season 02/cool_show.mkv";
        String path = FileMover.getTargetPath("/path/to/show/dir", "Season %2s", 2, "cool_show.mkv");

        assertEquals(expected, path);
    }

    @Test
    public void testGetTargetPathFileSeparatorInShowDir (){
        String expected = "/path/to/show/dir/Season 02/cool_show.mkv";
        String path = FileMover.getTargetPath("/path/to/show/dir/", "Season %2s", 2, "cool_show.mkv");

        assertEquals(expected, path);
    }

    @Test
    public void testGetTargetPathSeasonPaddingThree (){
        String expected = "/path/to/show/dir/Season 002/cool_show.mkv";
        String path = FileMover.getTargetPath("/path/to/show/dir", "Season %3s", 2, "cool_show.mkv");

        assertEquals(expected, path);
    }

    @Test
    public void testGetTargetPathSeasonPaddingOne (){
        String expected = "/path/to/show/dir/Season 2/cool_show.mkv";
        String path = FileMover.getTargetPath("/path/to/show/dir", "Season %1s", 2, "cool_show.mkv");

        assertEquals(expected, path);
    }

    @Test
    public void testGetTargetPathSeasonPaddingOneLongSeasonNumber (){
        String expected = "/path/to/show/dir/Season 22/cool_show.mkv";
        String path = FileMover.getTargetPath("/path/to/show/dir", "Season %1s", 22, "cool_show.mkv");

        assertEquals(expected, path);
    }

    @Test
    public void testGetTargetPathEmptySeasonFormat (){
        String expected = "/path/to/show/dir/cool_show.mkv";
        String path = FileMover.getTargetPath("/path/to/show/dir", "", 2, "cool_show.mkv");

        assertEquals(expected, path);
    }

    @Test
    public void testGetTargetPathSeasonFormatWithoutFormatString (){
        String expected = "/path/to/show/dir/Season/cool_show.mkv";
        String path = FileMover.getTargetPath("/path/to/show/dir", "Season", 2, "cool_show.mkv");

        assertEquals(expected, path);
    }
}
