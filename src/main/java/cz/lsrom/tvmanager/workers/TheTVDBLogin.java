package cz.lsrom.tvmanager.workers;

/**
 * This class is the holder of TheTVDB login information. TheTVDB API requires application to login before it can make
 * request against this API. This object is created with application API Key and converted to JSON for sending to TheTVD
 * API server as authentication request.
 *
 * @apiNote Only the 'apikey' is required for authentication, other fields can be left blank.
 *
 * Created by lsrom on 11/10/16.
 */
public class TheTVDBLogin {
    private String apikey;      // this is the application API key which is required for authentication
    private String username;    // required only for /user routes which is not important for this app
    private String userkey;     // required only with username field which is not mandatory

    /**
     * Create new object holding TheTVDB authentication details.
     *
     * @see <url>https://api.thetvdb.com/swagger#</url>
     *
     * @param apikey Only mandatory field. Must be set to get the API token which is needed for any subsequent API calls.
     * @param username Can be left blank.
     * @param userkey Can be left blank.
     */
    public TheTVDBLogin(String apikey, String username, String userkey) {
        this.apikey = apikey;
        this.username = username;
        this.userkey = userkey;
    }

    public String getApikey() {
        return apikey;
    }

    public String getUsername() {
        return username;
    }

    public String getUserkey() {
        return userkey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserkey(String userkey) {
        this.userkey = userkey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TheTVDBLogin that = (TheTVDBLogin) o;

        if (!apikey.equals(that.apikey)) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        return !(userkey != null ? !userkey.equals(that.userkey) : that.userkey != null);

    }

    @Override
    public int hashCode() {
        int result = apikey.hashCode();
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (userkey != null ? userkey.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TheTVDBLogin{");
        sb.append("apikey='").append(apikey).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", userkey='").append(userkey).append('\'');
        sb.append('}');
        return sb.toString();
    }
}