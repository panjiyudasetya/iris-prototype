package nl.sense_os.iris_android.task;

import org.json.JSONObject;

/**
 * Created by panjiyudasetya on 12/27/16.
 */

public interface TaskConverter {
    /** A contract method to convert an object into json object */
    JSONObject toJsonObject();
}
