package cz.lsrom.tvmanager.workers;

/**
 * Holds the JWT token from TheTVDB API. Token expires after 24 hours.
 * @see <url>https://api.thetvdb.com/swagger#/Authentication</url>
 * Created by lsrom on 11/10/16.
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JWTToken jwtToken = (JWTToken) o;

        return token.equals(jwtToken.token);

    }

    @Override
    public int hashCode() {
        return token.hashCode();
    }

    @Override
    public String toString() {
        return token;
    }
}