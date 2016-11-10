package cz.lsrom.tvmanager.workers;

import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by lsrom on 11/10/16.
 */
public class TheTVDBProvider {
    private static final String THETVDB_API_KEY = "FA55DD7926C17088";
    private static final String LOGIN_URL = "https://api.thetvdb.com/login";

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
     * @return
     */
    public static TheTVDBProvider login (){
        Gson gson = new Gson();
        TheTVDBLogin login = new TheTVDBLogin(THETVDB_API_KEY, "", "");     // create TVDB login object with API key

        String authenticationJson = gson.toJson(login);                     // convert TVDB login object to JSON
        HttpURLConnection http = null;

        try {   // create HTTP connection with POST method for retrieving the JWT token
            URL loginUrl = new URL(LOGIN_URL);
            URLConnection connection = loginUrl.openConnection();
            http = (HttpURLConnection) connection;

            http.setRequestMethod("POST");
            http.setDoOutput(true);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (http != null){
            byte[] jsonBytes = authenticationJson.getBytes();
            int jsonBytesLength = jsonBytes.length;

            http.setFixedLengthStreamingMode(jsonBytesLength);      // set length of HTTP equest
            http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            try {
                http.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // write request for token to TheTVDB
            try (OutputStream os = http.getOutputStream()){
                os.write(jsonBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }

            JWTToken token = null;     // new token object for holding the token value

            // read and parse response from TheTVDB with the JWT token
            try (InputStream is = http.getInputStream()){
                Gson tokenJSon = new Gson();

                // create token object from input stream
                token = tokenJSon.fromJson(getStringFromInputStream(is), JWTToken.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // if all went well and the token is set, return new TVDBProvider object
            if (token != null){
                return new TheTVDBProvider(token);
            }
        }

        // if something went wrong and we didn't acquire the JWT token, return null;
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
