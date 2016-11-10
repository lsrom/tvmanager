package cz.lsrom.tvmanager.workers;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by lsrom on 11/10/16.
 */
public class TheTVDBPRoviderTest {
    @Test
    public void testLogin() throws Exception {
        TheTVDBProvider provider = TheTVDBProvider.login();

        assertNotNull(provider);                                // if not null then login() method worked
        assertNotNull(provider.getToken());                     // check that there actually is token set
        assertNotNull(provider.getToken().getToken());          // check that the token is not null
        assertNotEquals(provider.getToken().getToken(), "");    // check that the token is not empty string
    }
}
