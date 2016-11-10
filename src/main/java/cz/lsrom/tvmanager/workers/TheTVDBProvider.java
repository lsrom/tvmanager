package cz.lsrom.tvmanager.workers;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.google.gson.Gson;
import com.sun.istack.internal.NotNull;
import cz.lsrom.tvmanager.model.Show;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;

/**
 * Created by lsrom on 11/10/16.
 */
public class TheTVDBProvider {
    private static Logger logger = LoggerFactory.getLogger(TheTVDBProvider.class);

    private static final String THETVDB_API_KEY = "FA55DD7926C17088";   // API key received from TheTVDB that should be used with this application
    private static final String BASE_URL = "https://api.thetvdb.com/";  // API URL of TheTVDB
    private static final String LOGIN_URL = BASE_URL + "login";         // on this URL application should attempt to login
    private static final String SEARCH_FOR_SHOW_URL = BASE_URL + "search/series?name="; // on this URL application can search for show using their name

    private static final int RESPONSE_CODE_TOKEN_INVALID = 401;
    private static final int RESPONSE_CODE_NOT_FOUND = 404;

    private JWTToken token;     // token acquired through authentication with TheTVDB API - must be included in all request

    private TheTVDBProvider(JWTToken token) {
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
            logger.error(e.getMessage());
        }

        JWTToken token = gson.fromJson(tokenJson, JWTToken.class);  // parse JSON with token into JWTToken object

        if (token != null){     // if new token object is not null, return new instance of this class
            return new TheTVDBProvider(token);
        }

        return null;            // if token wasn't successfully created return null
    }

    /**
     * Search for TV show in TheTVDB database. If show is found than new Show object is returned. Otherwise it will
     * return null.
     *
     * Returned show object will have the fields 'id', 'title', 'overview' and 'status' set.
     *
     * @param showTitle Title of the show to be searched for in TheTVDB database.
     * @return New Show object with fields populated from TheTVDB results.
     */
    public Show searchForShow (@NotNull String showTitle){
        String showResult = "";     // this variable will hold JSON response from TheTVDB

        try {
            String urlEncodeShow = URLEncoder.encode(showTitle.toLowerCase(), "UTF-8");     // encode show title in lowercase
            showResult = connect(SEARCH_FOR_SHOW_URL + urlEncodeShow, token.getToken());    // get JSON response
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        // parse response to new Show object
        return parseJsonToShow(showResult);
    }

    /**
     * Rreturns JWT token obtained from TheTVDB API. This token needs to be included in every request send to TheTVDB
     * API server.
     *
     * @return JWTToken object with TheTVDB authorization token.
     */
    public JWTToken getToken() {
        return token;
    }

    /**
     * This method connects to TheTVDB servers and gets data from them. If the token parameter is null this method
     * presumes that the token needs to be retrieved and uses POST method to get it as described on TheTVDB API page.
     * In such case the returned string is JSON with the JWT token.
     *
     * If token parameter is specified then this method presumes that data are about to be retrieved from the server and
     * uses GET method to retrieve it. JSON with this data is returned.
     *
     * If connection fails and/or no data are retrieved, empty string is returned.
     *
     * @param url URL which this method should access.
     * @param token JWT authorization token - if null or empty, new one is retrieved.
     * @return String with data received from the server or empty string if there was no such data.
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

        // check for response code
        switch (http.getResponseCode()){
            case RESPONSE_CODE_TOKEN_INVALID:   // token is missing or has expired
                logger.warn("JWT token missing or expired.");
                break;
            case RESPONSE_CODE_NOT_FOUND:       // show was not found
                logger.warn("TV show {} not found. Response: {}.", url, RESPONSE_CODE_NOT_FOUND);
                return "";
        }

        // read and parse response from TheTVDB - return string
        try (InputStream is = http.getInputStream()){
            return getStringFromInputStream(is);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        // if something went wrong return empty string
        return "";
    }

    /**
     * Converts JSON data to Show object. If passed argument is null or empty, then it returns null as well.
     * Otherwise it will return new Show object with attributes id, title, overview and status set.
     *
     * @param jsonShow String with JSON response from TheTVDB server.
     * @return New Show object or null.
     */
    private static Show parseJsonToShow (@NotNull String jsonShow){
        if (jsonShow == null || jsonShow.isEmpty()){
            return null;
        }

        // these values here will hold data parsed from JSON
        // JSON can't be parsed directly into Show using Gson because Show structure is not identical with the JSON data
        String showTitle = null;
        String id = null;
        String overview = null;
        String status = null;

        // parse JSON and get only the first array in data as that's the one holding the required information
        JsonObject metadata = Json.parse(jsonShow).asObject().get("data").asArray().get(0).asObject();

        showTitle = metadata.getString("seriesName", "");
        id = String.valueOf(metadata.get("id").asInt());    // can't be returned as string since it'S stored as int
        overview = metadata.getString("overview", "");
        status = metadata.getString("status", "");

        // create new show with values from JSON
        return new Show(showTitle, null, id, overview, status);
    }

    /**
     * Convert InputStream to String.
     *
     * @see <url>http://stackoverflow.com/a/35446009/4751720</url>
     * @param inputStream InputStream to be converted to String.
     * @return String with the content of an InputStream or empty string if InputStream is null.
     * @throws IOException If the first byte cannot be read fr other reason than the end of file.
     */
    private static String getStringFromInputStream(InputStream inputStream) throws IOException {
        if (inputStream == null){
            return "";
        }

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }
}
