package nl.sense_os.iris_android.js.executor;

import org.json.JSONException;
import org.json.JSONObject;

import nl.sense_os.iris_android.task.store.Task;

/**
 * Created by panjiyudasetya on 12/23/16.
 */

public class JSTask extends JSTaskExports {
    private String id;
    private String jsCode;
    private String action;

    public JSTask() {}

    public JSTask(Task task) {
        id = task.getId();
        jsCode = task.getJsCode();
        action = task.getAction();
        trigger_completed = 0;
        context_completed = 0;
    }

    // This method is used only by tests
    public JSTask(JSONObject taskJSON) throws JSONException {
        id = taskJSON.getString("id");
        jsCode = taskJSON.getString("jsCode");
        action = taskJSON.getString("action");
        trigger_completed = taskJSON.getDouble("trigger_completed");
        context_completed = taskJSON.getDouble("context_completed");
    }
}
