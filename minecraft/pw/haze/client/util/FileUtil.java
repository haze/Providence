package pw.haze.client.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * |> Author: haze
 * |> Since: 4/9/16
 */
public class FileUtil {

    public static boolean isFileEmpty(File f){
        try {
            return new BufferedReader(new FileReader(f)).readLine() == null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


}
