package cz.lsrom.tvmanager.workers;

import com.google.gson.Gson;
import com.sun.istack.internal.NotNull;

import java.io.*;
import java.net.*;

/**
 * Created by lsrom on 11/10/16.
 */
public class TheTVDBProvider {
    private static final String THETVDB_API_KEY = "FA55DD7926C17088";
    private static final String BASE_URL = "https://api.thetvdb.com/";
    private static final String LOGIN_URL = BASE_URL + "login";
    private static final String SEARCH_FOR_SHOW_URL = BASE_URL + "search/series?name=";

    private JWTToken token;     // token acquired through authentication with TheTVDB API - must be included in all request

    public TheTVDBProvider(JWTToken token) {
        this.token = token;
    }

    /**
     * This method will retrieve the JWT token from TheTVDB server. It has to be called before any calls to
     * TheTVDB database can be made.
     *
     * In case of success this method will return new TheTVDBProvider object that can be used to make request against
     * TheTVDB API.
     *
     * In case of failure, when no token is acquired,
     * @return New TheTVDBProvider object or null in case of failure.
     */
    public static TheTVDBProvider login (){
        Gson gson = new Gson();
        String tokenJson = null;
        try {
            tokenJson = connect(LOGIN_URL, null);   // try to acquire token json
        } catch (IOException e) {
            e.printStackTrace();
        }

        JWTToken token = gson.fromJson(tokenJson, JWTToken.class);  // parse JSON with token into JWTToken object

        if (token != null){     // if new token object is not null, return new instance of this class
            return new TheTVDBProvider(token);
        }

        return null;            // if token wasn't successfully created return null
    }

    /**
     * This method connects to TheTVDB servers and gets data from them. If the token parameter is null this method
     * presumes that the token needs to be retrieved and uses POST method to get it as described on TheTVDB API page.
     * In such casse the returned string is JSON with the JWT token.
     *
     * If token parameter is specified then this method presumes that data are about to be retrieved from the server and
     * uses GET method to retrieve it. JSON wth this data is returned.
     *
     * If connection fails and/or no data are retrieved, null is returned.
     *
     * @param url URL which this method should access.
     * @param token JWT authorization token - if null or empty, new one is retrieved.
     * @return String with data received from the server or null if there was no such data.
     * @throws IOException When connection to server fails.
     */
    private static String connect(@NotNull String url, String token) throws IOException {
        Gson gson = new Gson();
        byte[] outputBytes = null;      // will hold output data for POST method
        int outputLength = 0;           // number of bytes of output data for POST method

        HttpURLConnection http = null;

        URL loginUrl = new URL(url);
        URLConnection connection = loginUrl.openConnection();
        http = (HttpURLConnection) connection;

        if (token == null || token.isEmpty()){  // if token is not passed into this method then we must retrieve one
            http.setRequestMethod("POST");      // retrieving the token requires POST method

            TheTVDBLogin login = new TheTVDBLogin(THETVDB_API_KEY, "", "");     // create TheTVDB login object with API key
            String authenticationJson = gson.toJson(login);                     // convert TheTVDB login object to JSON

            outputBytes = authenticationJson.getBytes();        // get bytes of JSON authentication request
            outputLength = outputBytes.length;

            http.setFixedLengthStreamingMode(outputLength);      // set length of HTTP request
            http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            http.setDoOutput(true);
        } else {        // if token is passed then we are logged in and want to retrieve information from TheTVDB API
            http.setRequestMethod("GET");   // communication with TheTVDB API requires GET method
            http.setDoOutput(true);
            http.setRequestProperty("Content-Type", "charset=UTF-8");
            http.setRequestProperty("Authorization", "Bearer " + token);    // every call to the API must have authentication field set
        }

        http.connect();

        if (outputBytes != null){       // if outputBytes is still null then there are no data to be send
            // write request for token to TheTVDB
            try (OutputStream os = http.getOutputStream()){
                os.write(outputBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // read and parse response from TheTVDB - return string
        try (InputStream is = http.getInputStream()){
            return getStringFromInputStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // if something went wrong return null
        return null;
    }

    public JWTToken getToken() {
        return token;
    }

    /**
     * Convert InputStream to String.
     *
     * @see <url>http://stackoverflow.com/a/35446009/4751720</url>
     * @param inputStream InputStream to be converted to String.
     * @return String with the content of an InputStream.
     * @throws IOException If the first byte cannot be read fr other reason than the end of file.
     */
    private static String getStringFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }
}
