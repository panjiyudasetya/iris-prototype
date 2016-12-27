package nl.sense_os.iris_android.task.store;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.BuildConfig;
import io.realm.RealmList;

/**
 * Created by panjiyudasetya on 12/23/16.
 */

public class ConversionUtils {

    /**
     * Converting tasks in json format into {@link List} of Task
     *
     * @param tasksJson tasks in json format
     * @return {@link List} of Task
     */
    @NonNull
    public static List<Task> convertJsonToTasks(@NonNull JSONArray tasksJson) {
        List<Task> tasks = new ArrayList<>();

        try {
            for (int i = 0; i < tasksJson.length(); i++) {
                JSONObject taskJson = tasksJson.getJSONObject(i);
                Task task = new Task(
                        taskJson.getString("id"),
                        taskJson.getString("action"),
                        taskJson.getString("desc"),
                        taskJson.getString("jsCode"),
                        getInputArray(taskJson.getJSONArray("requirements"))
                );

                tasks.add(task);
            }
        } catch (JSONException ex) {
            if (BuildConfig.DEBUG)      ex.printStackTrace();
        }
        return tasks;
    }

    /**
     * Get Inputs from given JSON Array
     *
     * @param requirements JSONArray of The Requirements
     * @return inputs
     */
    private static RealmList<Input> getInputArray(JSONArray requirements) {
        RealmList<Input> inputArray = new RealmList<>();

        try {
            for (int i = 0; i < requirements.length(); i++) {
                JSONObject requirement = requirements.getJSONObject(i);

                // TODO: import frequency as well. Should this have version info?
                Input input = new Input();
                input.setName(requirement.getString("name"));

                inputArray.add(input);
            }
        } catch (JSONException ex) {
            if (BuildConfig.DEBUG)      ex.printStackTrace();
        }
        return inputArray;
    }
}
