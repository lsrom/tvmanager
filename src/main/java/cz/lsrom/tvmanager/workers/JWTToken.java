package cz.lsrom.tvmanager.workers;

/**
 * Created by lsrom on 11/10/16.
 */
/**
 * Holds the JWT token from TheTVDB API. Token expires after 24 hours.
 * @see <url>https://api.thetvdb.com/swagger#/Authentication</url>
 */
public class JWTToken {
    private String token;   // string with token

    /**
     * Create new token holder with token value.
     * @param token JWT token string from TheTVDB authentication process.
     */
    public JWTToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}