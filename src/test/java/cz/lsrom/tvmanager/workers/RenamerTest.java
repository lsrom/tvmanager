package cz.lsrom.tvmanager.workers;

import cz.lsrom.tvmanager.model.EpisodeFile;
import cz.lsrom.tvmanager.model.PreferencesHandler;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by lsrom on 12/1/16.
 */
public class RenamerTest {
    private static String renameStringPadding1 = "%S s%1se%1e %t";
    private static String renameStringPadding2 = "%S s%2se%2e %t";
    private static String renameStringPadding3 = "%S s%3se%3e %t";
    private static String renameStringResolution = "%S s%2se%2e %r %t";

    /*
    * NOTE: It is important to create File in EpisodeFile with some name and EXTENSION. For example: test.mkv.
    * This is because the renaming service uses it to get file extension for new filename.
    *
    * */
    private final TestInput[] testInputs = {
            new TestInput(new EpisodeFile("Dragon Ball", "76666", 1, 3, "720p", "", new File("db.mkv")), "Dragon Ball s01e03 The Nimbus Cloud of Roshi.mkv", renameStringPadding2),
            new TestInput(new EpisodeFile("Dragon Ball", "76666", 4, 16, "720p", "", new File("db.mkv")), "Dragon Ball s4e16 The Ultimate Sacrifice.mkv", renameStringPadding1),
            new TestInput(new EpisodeFile("Dragon Ball Z", "81472", 2, 2, "720p", "", new File("dbz.mkv")), "Dragon Ball Z s002e002 Look Out Below.mkv", renameStringPadding3),
            new TestInput(new EpisodeFile("Dragon Ball Z", "81472", 9, 38, "720p", "", new File("dbz.mkv")), "Dragon Ball Z s09e38 Goku's Next Journey.mkv", renameStringPadding2),
            new TestInput(new EpisodeFile("Dragon Ball Super", "295068", 1, 1, "720p", "", new File("dbs.mkv")), "Dragon Ball Super s01e01 The Peace Prize. Who'll Get the 100 Million Zeny!.mkv", renameStringPadding2),
            new TestInput(new EpisodeFile("Dragon Ball Super", "295068", 3, 17, "720p", "", new File("dbs.mkv")), "Dragon Ball Super s003e017 The Seal on Planet Pot-au-feu - The Secret of the Unleashed Superhuman Water!.mkv", renameStringPadding3),
            new TestInput(new EpisodeFile("Lucifer", "295685", 2, 7, "720p", "", new File("lucifer.mkv")), "Lucifer s02e07 My Little Monkey.mkv", renameStringPadding2),
            new TestInput(new EpisodeFile("Gossip Girl", "80547", 3, 15, "720p", "", new File("gg.mkv")), "Gossip Girl s03e15 The Sixteen Year Old Virgin.mkv", renameStringPadding2),
            new TestInput(new EpisodeFile("Smallville", "72218", 7, 15, "720p", "", new File("smallville.mkv")), "Smallville s07e15 720p Veritas.mkv", renameStringResolution),
            new TestInput(new EpisodeFile("Star Gate SG1", "72449", 5, 5, "720p", "", new File("sg1.mkv")), "Star Gate SG1 s05e05 720p Red Sky.mkv", renameStringResolution),
            new TestInput(new EpisodeFile("Star Gate Atlantis", "70851", 3, 8, "720p", "", new File("atlantis.mkv")), "Star Gate Atlantis s03e08 720p McKay and Mrs. Miller.mkv", renameStringResolution),
    };

    private Renamer renamer;

    @Before
    public void setUp () throws IOException {
        renamer = new Renamer(PreferencesHandler.loadPreferences());
    }

    @Test
    public void testRename (){
        for (TestInput testInput : testInputs){
            renamer.addShow(testInput.episodeFile);
            String newFileName = renamer.getNewFileName(testInput.episodeFile, testInput.renameString);

            assertNotNull(newFileName);

            assertEquals(testInput.expectedFileName, newFileName);
        }
    }

    @Test
    public void testAddShowWhichNeedsOneRetry (){
        EpisodeFile ef = new EpisodeFile(
                "Naruto Shippuuden 006",
                "79824",
                1,
                6,
                "",
                "",
                new File("naruto.mkv")
        );

        renamer.addShow(ef);

        String newFilename = renamer.getNewFileName(ef, renameStringPadding1);

        assertEquals("Naruto Shippuuden s1e6 Mission Cleared.mkv", newFilename);
    }

    @Test
    public void testAddShowWhichNeedsTwoRetries (){
        EpisodeFile ef = new EpisodeFile(
                "Naruto Shippuuden 006 02",
                "79824",
                1,
                6,
                "",
                "",
                new File("naruto.mkv")
        );

        renamer.addShow(ef);

        String newFilename = renamer.getNewFileName(ef, renameStringPadding1);

        assertEquals("Naruto Shippuuden s1e6 Mission Cleared.mkv", newFilename);
    }

    @Test
    public void testAddShowWhichNeedsThreeRetries (){
        EpisodeFile ef = new EpisodeFile(
                "Naruto Shippuuden 006 03 00",
                "79824",
                1,
                6,
                "",
                "",
                new File("naruto.mkv")
        );

        renamer.addShow(ef);

        String newFilename = renamer.getNewFileName(ef, renameStringPadding1);

        assertEquals("Naruto Shippuuden s1e6 Mission Cleared.mkv", newFilename);
    }

    private class TestInput {
        public final EpisodeFile episodeFile;
        public final String expectedFileName;
        public final String renameString;

        public TestInput(EpisodeFile episodeFile, String expectedFileName, String renameString) {
            this.episodeFile = episodeFile;
            this.expectedFileName = expectedFileName;
            this.renameString = renameString;
        }
    }
}
