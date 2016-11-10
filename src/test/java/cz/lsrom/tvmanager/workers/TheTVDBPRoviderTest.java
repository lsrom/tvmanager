package cz.lsrom.tvmanager.workers;

import cz.lsrom.tvmanager.model.Show;
import org.junit.Test;
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
        assertNotEquals(tvdbProvider.getToken().getToken(), "");    // check that the token is not empty string
    }

    @Test
    public void testSearchForShow() throws Exception {
        if (tvdbProvider == null){      // if this is the first test method called, create new provider
            tvdbProvider = TheTVDBProvider.login();
        }

        Show show = tvdbProvider.searchForShow("Dragon Ball");

        assertNotNull(show);
        assertEquals(show.getId(), "76666");
        assertEquals(show.getTitle(), "Dragon Ball");
        assertNotNull(show.getOverview());
        assertEquals(show.getStatus(), "Ended");
    }

    @Test
    public void testSearchForShowWhichDoesntExist() throws Exception {
        if (tvdbProvider == null){      // if this is the first test method called, create new provider
            tvdbProvider = TheTVDBProvider.login();
        }

        Show show = tvdbProvider.searchForShow("This is show that should exist...");

        assertNull(show);
    }
}
