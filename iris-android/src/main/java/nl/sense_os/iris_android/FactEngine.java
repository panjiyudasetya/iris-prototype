package nl.sense_os.iris_android;

import android.support.annotation.NonNull;
import android.util.Log;
import org.json.JSONObject;
import nl.sense_os.iris_android.task.store.Input;
import nl.sense_os.iris_android.task.store.Task;

/**
 * Created by panjiyudasetya on 12/23/16.
 */

public class FactEngine {
    private static final String TAG = "[FactEngine]";

    // It needs to be NSObject in order to keep the value compatible in JavaScriptCore
    private JSONObject facts;

    public FactEngine() {
        facts = new JSONObject();
    }

    // Task is the parameter because in the future, we might want to persist the state of tasks
    public JSONObject getInputs(Task task) {
        JSONObject taskInputs = new JSONObject();

        // TODO: We need to get the current status (trigger_completed, context_completed)
        // How to relate these status with Task
        // facts[task.id]??
        // facts[goal.id]??

        for (Input requirement : task.getRequirements()) {
            String value = null;

            // TODO : Do we need to maintain the exception ?
            try {
                value = facts.getString(requirement.getName());
                taskInputs.put(requirement.getName(), value);
            } catch (Exception ex) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, ex.getMessage());
                }
                continue;
            }
        }
        return taskInputs;
    }

    public void insert(@NonNull JSONObject value, @NonNull String name) {
        try {
            // Values should be validated against schema by the data producer itself.
            // Hence, no validation here.
            if (BuildConfig.DEBUG) {
                Log.d(TAG, String.format("inserting a new value:%s for %s", value, name));
            }
            facts.put(name, value);
        } catch (Exception ex) {
            Log.e(TAG,
                    String.format("%s exception happened while inserting a new value:%s for %s",
                            ex.getMessage(), value, name)
            );
        }
    }
}
