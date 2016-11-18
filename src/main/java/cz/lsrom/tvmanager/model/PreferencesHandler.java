package cz.lsrom.tvmanager.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by lsrom on 11/17/16.
 */
public abstract class PreferencesHandler {
    private static Logger logger = LoggerFactory.getLogger(PreferencesHandler.class);

    private static Path preferencesPath = Paths.get(System.getProperty("user.home") + "/.tvmanager/preferences.json");
    private static Gson gson = new Gson();

    public static Preferences loadPreferences (){
        String pref;
        Preferences preferences = new Preferences();
        try {
            pref = new String(Files.readAllBytes(preferencesPath));

            preferences = gson.fromJson(pref, Preferences.class);

            return preferences;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return preferences;
        }
    }

    public static void savePreferences (Preferences preferences) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(preferences);

        // if directory doesn't exist, create it
        if (!Files.exists(Paths.get(System.getProperty("user.home") + "/.tvmanager"))){
            Files.createDirectory(Paths.get(System.getProperty("user.home") + "/.tvmanager"));
        }

        // write preferences to hte file, overwriting any existing one
        Files.write(preferencesPath, json.getBytes());
    }

    /**
     * Checks if this computer has preferences file. If so, returns TRUE, otherwise FALSE.
     * @return TRUE if file with preferences can be read, otherwise FALSE.
     */
    public static boolean preferencesExist (){
        return Files.exists(preferencesPath);
    }
}
