package cz.lsrom.tvmanager.workers;

import cz.lsrom.tvmanager.TestInput;

import java.io.File;

/**
 * Created by lsrom on 11/23/16.
 */
public abstract class TestInputList {
    public static final TestInput[] testInputs = {
            new TestInput(new File("Lucifer.S02E07.720p.HDTV.X264-DIMENSION[ettv].mkv"), "Lucifer", 2, 7, "My Little Monkey", "720p", ""),
            new TestInput(new File("/data/shows/Lucifer.S02E07.720p.HDTV.X264-DIMENSION[ettv].mkv"), "Lucifer", 2, 7, "My Little Monkey", "720p", "/data/shows"),
            new TestInput(new File("C://Users/MorganFreeman/Lucifer.S02E07.720p.HDTV.X264-DIMENSION[ettv].mkv"), "Lucifer", 2, 7, "My Little Monkey", "720p", "C:/Users/MorganFreeman"),

            new TestInput(new File("Supernatural.S06E05.Live.Free.Or.Twihard.480p.BluRay.x264.AC3.5.1-LeRalouf.mkv"), "Supernatural", 6, 5, "Live Free or Twihard", "480p", ""),
            new TestInput(new File("Supernatural.S04E01.Lazarus.Rising.720p.BluRay.x264.AC3.5.1-LeRalouf.mkv"), "Supernatural", 4, 1, "Lazarues Rising", "720p", ""),
            new TestInput(new File("Supernatural.S04E22.Lucifer.Rising.1080p.BluRay.x264.AC3.5.1-LeRalouf.mkv"), "Supernatural", 4, 22, "Lucifer Rising", "1080p", ""),

            new TestInput(new File("[a-s]_dragon_ball_-_105_-_here_comes_yajirobe__rs2_[7f781c7e].mkv"), "Dragon Ball", -1, 105, "Here Comes Yajirobe", "", ""),
            new TestInput(new File("[a-s]_dragon_ball_-_116v2_-_a_taste_of_destiny__rs2_[4d4b0e7e].mkv"), "Dragon Ball", -1, 116, "A Taste of Destiny", "", ""),
            new TestInput(new File("[AnimeRG] Dragon Ball Super - 001 [1080p] [x265] [pseudo].mkv"), "Dragon Ball Super", -1, 1, "The Peace Prize. Who'll Get the 100 Million Zeny!", "1080p", ""),
            new TestInput(new File("[PA]-Dragon-Ball-Z-001-[1080p][Hi8b][3849F7F5][V2]-(1).mkv"), "Dragon Ball Z", -1, 1, "The New Threat", "1080p", ""),
            new TestInput(new File("One-Punch-Man 001 [1080p].mkv"), "One Punch Man", -1, 1, "The Strongest Man", "1080p", ""),

            new TestInput(new File("warehouse.13.s1e01.720p.hdtv.x264-dimension.mkv"), "Warehouse 13", 1, 1, "Title", "720p", ""),
            new TestInput(new File("24.s08.e01.720p.hdtv.x264-immerse.mkv"), "24", 8, 1, "Title", "720p", ""),
            new TestInput(new File("JAG.S10E01.DVDRip.XviD-P0W4DVD.avi"), "JAG", 10, 1, "Title", "", ""),
            new TestInput(new File("gossip.girl.s03e15.hdtv.xvid-fqm.avi"), "Gossip Girl", 3, 15, "Title", "", ""),
            new TestInput(new File("smallville.s09e15.hdtv.xvid-2hd.avi"), "Smallville", 9, 15, "Title", "", ""),
            new TestInput(new File("the.big.bang.theory.s03e18.720p.hdtv.x264-ctu.mkv"), "The Big Bang Theory", 3, 18, "Title", "720p", ""),
            new TestInput(new File("Lost.S06E05.Lighthouse.DD51.720p.WEB-DL.AVC-FUSiON.mkv"), "Lost", 6, 5, "Title", "720p", ""),
            new TestInput(new File("castle.2009.s01e09.720p.hdtv.x264-ctu.mkv"), "Castle 2009", 1, 9, "Title", "720p", ""),
    };
}