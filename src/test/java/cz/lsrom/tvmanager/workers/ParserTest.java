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

    @Test
    public void testParse() throws Exception {
        for (TestInput testInput : TestInputList.testInputs){
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
