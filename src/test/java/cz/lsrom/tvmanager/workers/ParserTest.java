package cz.lsrom.tvmanager.workers;

import cz.lsrom.tvmanager.TestInput;
import cz.lsrom.tvmanager.model.EpisodeFile;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsrom on 11/9/16.
 */
public class ParserTest {
    public static final List<TestInput> testInputList = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        // test that parsing works with and without the directory
        testInputList.add(new TestInput(new File("Lucifer.S02E07.720p.HDTV.X264-DIMENSION[ettv].mkv"), "Lucifer", 2, 7, "My Little Monkey", "720p", ""));
        testInputList.add(new TestInput(new File("/data/shows/Lucifer.S02E07.720p.HDTV.X264-DIMENSION[ettv].mkv"), "Lucifer", 2, 7, "My Little Monkey", "720p", "/data/shows"));
        testInputList.add(new TestInput(new File("C://Users/MorganFreeman/Lucifer.S02E07.720p.HDTV.X264-DIMENSION[ettv].mkv"), "Lucifer", 2, 7, "My Little Monkey", "720p", "C:/Users/MorganFreeman"));

        testInputList.add(new TestInput(new File("Supernatural.S06E05.Live.Free.Or.Twihard.480p.BluRay.x264.AC3.5.1-LeRalouf.mkv"), "Supernatural", 6, 5, "Live Free or Twihard", "480p", ""));
        testInputList.add(new TestInput(new File("Supernatural.S04E01.Lazarus.Rising.720p.BluRay.x264.AC3.5.1-LeRalouf.mkv"), "Supernatural", 4, 1, "Lazarues Rising", "720p", ""));
        testInputList.add(new TestInput(new File("Supernatural.S04E22.Lucifer.Rising.1080p.BluRay.x264.AC3.5.1-LeRalouf.mkv"), "Supernatural", 4, 22, "Lucifer Rising", "1080p", ""));

        testInputList.add(new TestInput(new File("[a-s]_dragon_ball_-_105_-_here_comes_yajirobe__rs2_[7f781c7e].mkv"), "dragon ball", -1, 105, "Here Comes Yajirobe", "", ""));
        testInputList.add(new TestInput(new File("[AnimeRG] Dragon Ball Super - 001 [1080p] [x265] [pseudo].mkv"), "Dragon Ball Super", -1, 1, "The Peace Prize. Who'll Get the 100 Million Zeny!", "1080p", ""));
        testInputList.add(new TestInput(new File("[PA]-Dragon-Ball-Z-001-[1080p][Hi8b][3849F7F5][V2]-(1).mkv"), "Dragon Ball Z", -1, 1, "The New Threat", "1080p", ""));
        testInputList.add(new TestInput(new File("One-Punch-Man 001 [1080p].mkv"), "One Punch Man", -1, 1, "The Strongest Man", "1080p", ""));
    }

    @Test
    public void testParse() throws Exception {
        for (TestInput testInput : testInputList){
            EpisodeFile episodeFile = Parser.parse(testInput.input);

            assertNotNull(episodeFile);
            assertEquals(testInput.showName, episodeFile.getShowName());
            assertEquals(testInput.season, episodeFile.getSeason());
            assertEquals(testInput.episodeNum, episodeFile.getEpisodeNum());
            assertEquals(testInput.resolution, episodeFile.getResolution());
            assertEquals(testInput.directory, episodeFile.getDirectory());
        }
    }
}