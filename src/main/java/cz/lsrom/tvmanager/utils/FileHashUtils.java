package cz.lsrom.tvmanager.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by lsrom on 1.4.2017.
 */
public class FileHashUtils {
    // to prevent instantiation
    private FileHashUtils (){}

    public static byte[] hashFile (final File file){
        if (file == null || !file.exists()){return null;}

        try {
            return HashUtils.sha256(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
