package nl.sense_os.iris_android.task.store;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import java.util.List;
import nl.sense_os.iris_android.Iris;
import nl.sense_os.iris_android.task.store.ErrorListener.DeletionInputErrorListener;
import nl.sense_os.iris_android.task.store.ErrorListener.DeletionActionErrorListener;

/**
 * Created by panjiyudasetya on 12/23/16.
 */

public class TaskStore {
    private Iris.Callback irisCallback;
    private TaskStorage taskStorage;
    private TaskLoader taskLoader;

    public TaskStore(@NonNull Iris.Callback irisCallback,
                     @NonNull TaskStorage taskStorage,
                     @NonNull TaskLoader taskLoader) {

        this.irisCallback = irisCallback;
        this.taskStorage = taskStorage;
        this.taskLoader = taskLoader;
    }

    public void loadTasks(ErrorListener.AdditionErrorListener errorListener) {
        List<Input> inputs = taskStorage.getAvailableInputs();
        List<Action> actions = taskStorage.getAvailableActions();
        List<Task> tasks = taskLoader.getTasks(inputs, actions);

        taskStorage.insertOrUpdateTasks(tasks, errorListener);
        // TODO: check if there are requirements that are changed and nofify
        //delegate?.onRequirementsUpdated(requirements: <#T##[String : Any]#>)
    }

    public List<Task> getTasks() {
        return taskStorage.getTasks();
    }

    public List<Task> getTasks(String byInputName) {
        // Returns Tasks as Task Object.
        return taskStorage.getTasks(byInputName);
    }

    public Task getTask(String byID) {
        return taskStorage.getTask(byID);
    }

    // TODO : do we need to also pass the frequency value as parameter?
    public void insertOrUpdateAvailableInput(String name) {
        taskStorage.insertOrUpdateAvailableInput(name, 0);
    }

    public JSONArray getAvailableInputs() {
        // TODO: should this conversion be done in one layer above?
        JSONArray output = new JSONArray();
        List<Input> availableInputs = taskStorage.getAvailableInputs();
        for (Input input : availableInputs) {
            output.put(input.toJsonObject());
        }
        return output;
    }

    public void deleteAvailableInput(@NonNull String name,
                                     @NonNull DeletionInputErrorListener errorListener) {

        taskStorage.deleteAvailableInput(name, errorListener);
    }

    public void insertOrUpdateAvailableActions(String name) {
        taskStorage.insertOrUpdateAvailableAction(name);
    }

    public JSONArray getAvailableActions() {
        // TODO: should this conversion be done in one layer above?
        JSONArray output = new JSONArray();
        List<Action> availableActions = taskStorage.getAvailableActions();
        for (Action action : availableActions) {
            output.put(action.toJsonObject());
        }
        return output;
    }

    public void deleteAvailableAction(@NonNull String name,
                                      @NonNull DeletionActionErrorListener errorListener) {

        taskStorage.deleteAvailableAction(name, errorListener);
    }

}
