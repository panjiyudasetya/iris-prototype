package nl.sense_os.iris_android.utils;

import android.support.annotation.NonNull;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by panjiyudasetya on 12/28/16.
 */

public class JSONKeysCollector {
    public static List<String> keysFrom(@NonNull JSONObject object) {
        List<String> result = new ArrayList<>();
        Iterator<String> keys = object.keys();
        while(keys.hasNext()) {
            System.out.println( keys.next() );
        }
        return result;
    }
}
