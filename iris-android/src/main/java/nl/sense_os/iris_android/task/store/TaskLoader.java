package nl.sense_os.iris_android.task.store;

import org.json.JSONArray;
import java.util.List;

/**
 * Created by panjiyudasetya on 12/23/16.
 */

public class TaskLoader {

    public List<Task> getTasks(List<Input> inputs, List<Action> actions) {
        // This method convert JSON to Task, so that the outside of this class does not have to know anything about JSON.
        // This way it is easier to change, when we decide not to use JSON anymore.
        // TODO: implement actual API call
        // ["name": "implement proper API call here"]
        JSONArray taskJson = new JSONArray();
        return ConversionUtils.convertJsonToTasks(taskJson);
    }
}
