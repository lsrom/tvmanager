package cz.lsrom.tvmanager.workers;

import cz.lsrom.tvmanager.model.Episode;
import cz.lsrom.tvmanager.model.Show;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by lsrom on 11/10/16.
 */
public class TheTVDBPRoviderTest {
    private TheTVDBProvider tvdbProvider;

    @Test
    public void testLogin() throws Exception {
        if (tvdbProvider == null){      // if this is the first test method called, create new provider
            tvdbProvider = TheTVDBProvider.login();
        }

        assertNotNull(tvdbProvider);                                // if not null then login() method worked
        assertNotNull(tvdbProvider.getToken());                     // check that there actually is token set
        assertNotNull(tvdbProvider.getToken().getToken());          // check that the token is not null
        assertNotEquals("", tvdbProvider.getToken().getToken());    // check that the token is not empty string
    }

    @Test
    public void testSearchForShow() throws Exception {
        if (tvdbProvider == null){      // if this is the first test method called, create new provider
            tvdbProvider = TheTVDBProvider.login();
        }

        Show show = tvdbProvider.searchForShow("Dragon Ball");

        assertNotNull(show);
        assertEquals("76666", show.getId());
        assertEquals("Dragon Ball", show.getTitle());
        assertNotNull(show.getOverview());
        assertEquals("Ended", show.getStatus());
    }

    @Test
    public void testSearchForShowWhichDoesntExist() throws Exception {
        if (tvdbProvider == null){      // if this is the first test method called, create new provider
            tvdbProvider = TheTVDBProvider.login();
        }

        Show show = tvdbProvider.searchForShow("This is show that shouldn't exist...");

        assertNull(show);
    }

    @Test
    public void testGetAllEpisodesForShow() throws Exception {
        if (tvdbProvider == null){      // if this is the first test method called, create new provider
            tvdbProvider = TheTVDBProvider.login();
        }

        List<Episode> list = tvdbProvider.getAllEpisodesForShow("76666");

        assertNotNull(list);                // check that returned value is not null
        assertTrue(list.size() == 153);     // check that there are all episode

        // check that episodes are in correct order
        assertEquals("Secret of the Dragon Balls", list.get(0).getTitle());
        assertEquals("The Legend of Goku", list.get(12).getTitle());
        assertEquals("Goku's Rival", list.get(13).getTitle());
        assertEquals("The Final Blow", list.get(27).getTitle());
        assertEquals("The Roaming Lake", list.get(28).getTitle());
        assertEquals("Danger in the Air", list.get(44).getTitle());
        assertEquals("Bulma's Bad Day", list.get(45).getTitle());
        assertEquals("The End, The Beginning", list.get(152).getTitle());
    }
}
