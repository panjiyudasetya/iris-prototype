package nl.sense_os.iris_android;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import nl.sense_os.iris_android.js.executor.JSExecutor;
import nl.sense_os.iris_android.js.executor.JSExecutor.JSExecutorErrorListener;
import nl.sense_os.iris_android.js.executor.JSTask;
import nl.sense_os.iris_android.task.store.ErrorListener.AdditionErrorListener;
import nl.sense_os.iris_android.task.store.ErrorListener.DeletionInputErrorListener;
import nl.sense_os.iris_android.task.store.ErrorListener.DeletionActionErrorListener;
import nl.sense_os.iris_android.task.store.Input;
import nl.sense_os.iris_android.task.store.Task;
import nl.sense_os.iris_android.task.store.TaskLoader;
import nl.sense_os.iris_android.task.store.TaskStorage;
import nl.sense_os.iris_android.task.store.TaskStore;
import nl.sense_os.iris_android.utils.JSONKeysCollector;

/**
 * Created by panjiyudasetya on 12/23/16.
 */

public abstract class Iris {
    private static final String TAG = "[IRIS]";

    public interface Callback {
        void onRequirementsUpdated(@NonNull JSONObject requirements);
        void onActionTriggered(@NonNull JSONObject actionPayload);
    }

    public static void tryJSRuntime() {
        JSExecutor.execute(new JSTask(), "");
    }

    private TaskStore taskStore;
    private FactEngine factEngine;
    private JSExecutor jsExecutor;

    public Iris(@NonNull Iris.Callback callback,
                @NonNull TaskStorage taskStorage,
                @NonNull TaskLoader taskLoader) {

        this.taskStore = new TaskStore(callback, taskStorage, taskLoader);
        this.factEngine = new FactEngine();
        this.jsExecutor = new JSExecutor(callback);
    }

    /**
     * Register or Update an available Input in Iris.
     * This method does NOT trigger loadTask automatically.
     * @param name name of the new available Input
     */
    public void registerInput(@NonNull String name){
        //TODO: add version number of the available Input
        taskStore.insertOrUpdateAvailableInput(name);
    }

    /**
     * Deregister an available Input in Iris.
     * This method does NOT trigger loadTask automatically.
     * Call deletion error listener when Input is not registered.
     *
     * @param name name of the TaskInput that should be removed
     * @param errorListener deletion error listener
     */
    public void deregisterInput(@NonNull String name,
                                @NonNull DeletionInputErrorListener errorListener) {
        // No version required, because the deletion only cares the name
        taskStore.deleteAvailableInput(name, errorListener);
    }

    /**
     * Returns an array of inputs that are registered.
     * <pre>
     * {@code
     *    [
     *      {"name": "time_active", "version": 3},
     *      {"name": "time", "version": 6}
     *    ]
     * }
     * </pre>
     * @return return an array of TaskInput in the format above.
     */
    public @NonNull JSONArray getAvailableInputs() {
        // Array of dictionary, because the external component shouldn't know anything about Action class.
        return taskStore.getAvailableInputs();
    }

    /**
     * Register or Update an available Action in Iris.
     * This method does NOT trigger loadTask automatically.
     *
     * @param name name of the new available Action
     */
    public void registerAction(String name) {
        //TODO: add version number of the available Action
        taskStore.insertOrUpdateAvailableActions(name);
    }

    /**
     * Deregister an available Action in Iris.
     * This method does NOT trigger loadTask automatically.
     * Call deletion action error listener when Action is not registered.
     *
     * @param name name of the Action that should be removed
     * @param errorListener deletion error listener
     */
    public void deregisterAction(String name, DeletionActionErrorListener errorListener) {
        // No version required, because the deletion only cares the name
        taskStore.deleteAvailableAction(name, errorListener);
    }

    /**
     * Returns an array of actions that are registered.
     * <pre>
     * {@code
     *    [
     *      {"name": "push_notification", "version": 3},
     *      {"name": "hit_url", "version": 2}
     *    ]
     * }
     * </pre>
     * @return return an array of Actions in the format above.
     */
    public @NonNull JSONArray getAvailableAction() {
        // Array of dictionary, because the external component shouldn't know anything about Action class.
        return taskStore.getAvailableActions();
    }

    /**
     * Returns an array of Tasks that are loaded locally in Dictionary format.
     * eg)
     * <pre>
     * {@code
     *    [
     *      {
     *          "desc" : "<description>",
     *          "jsCode" : "<javascript code>",
     *          "action" : "<actionPayload>",
     *          "id" : "<taskID>",
     *          "requirements" : [
     *              {
     *                  "frequency" : 0,
     *                  "name" : "time_active",
     *                  "version": 3
     *              },
     *              {
     *                  "frequency" : 0,
     *                  "name" : "physical_location_sensor",
     *                  "version": 2
     *              }
     *           ]
     *      }
     *    ]
     * }
     * </pre>
     * @return return an array of Task in the format above.
     */
    public @NonNull JSONArray getTasks() {
        // Array of dictionary, because the external component shouldn't know anything about Task class.
        JSONArray tasksArray = new JSONArray();
        List<Task> tasks = taskStore.getTasks();
        for (Task task : tasks) {
            tasksArray.put(task.toJsonObject());
        }
        return tasksArray;
    }

    /**
     * Load Tasks from Remote server based on the available TaskInputs and Actions.
     * Called error listener when try to load the task from storage
     * @param errorListener
     */
    public void loadTasks(@NonNull AdditionErrorListener errorListener) {
        taskStore.loadTasks(errorListener);
    }

    /**
     * Transmit the data to Iris. Iris will evaluate the relevant tasks. Triggers action if condition matches
     * @param inputName Name of the input
     * @param data value of the input
     * @param errorListener JSExecutor error listener
     */
    public void onNewData(@NonNull String inputName,
                          @NonNull JSONObject data,
                          @NonNull JSExecutorErrorListener errorListener) {

        factEngine.insert(data, inputName);
        // Retrieve all the task whose requirements contain the input
        List<Task> tasks = taskStore.getTasks(inputName);
        for (Task task : tasks) {
            // Retrieve all the input in requirements
            // TODO: If we don't have good reason to pass task object, pass requirements.
            //       For now, it is passing task because we might use something else from requirements.
            JSONObject inputs = factEngine.getInputs(task);

            // TODO: If we don't have good reason to pass task object, pass requirements.
            if (hasAllRequiredInputs(inputs, task)) {
                JSTask jsTask = new JSTask(task);

                // TODO : Do we need anonymous class for JSExecutorErrorListener instead of pass from the parameter ?
                // The purpose is to break the loop if the executor has been failed.
                /*jsExecutor.execute(jsTask, inputs, new JSExecutorErrorListener() {
                    @Override
                    public void incorrectFormatInPayload() {
                        // Do something here
                        return;
                    }

                    @Override
                    public void payloadIsUnknown() {
                        // Do something here
                        return;
                    }
                });*/
                jsExecutor.execute(jsTask, inputs, errorListener);
            }
        }
    }

    /**
     * Check if all the requirements are given as inputs.
     * @param inputs a dictionary of inputs
     * @param task Task object. //TODO: this could be requirements?
     * @return True if all the required inputs are provided, False otherwise
     */
    public boolean hasAllRequiredInputs(@NonNull JSONObject inputs, @NonNull Task task) {
        List<String> inputNames = JSONKeysCollector.keysFrom(inputs);
        List<Input> requirements = task.getRequirements();
        for (Input requirement : requirements) {
            if (!inputNames.contains(requirement.getName())) {
                if (BuildConfig.DEBUG) {
                    String message = String.format("Not all the requirements provided.\nRequired : %s\nProvided: %s",
                            requirements, inputNames);
                    Log.d(TAG, message);
                }
                return false;
            }
        }
        return true;
    }
}
