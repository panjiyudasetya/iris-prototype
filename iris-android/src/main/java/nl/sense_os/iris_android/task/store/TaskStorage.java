package nl.sense_os.iris_android.task.store;

/**
 * Created by panjiyudasetya on 12/23/16.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import io.realm.BuildConfig;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import nl.sense_os.iris_android.RealmModule;

/**
 * TaskStorage uses Realm as the underlying data structure.
 * Tasks should be able to be quickly queried based on input. And also Inputs should be able to be queried based on Tasks.
 * (For subscribing inputs on MessageBus when a new Task is loaded or requirements changes)
 *
 * Dictionary can be used but it will cause us to do double book keeping to maintain the bidirectional relationship.
 * After a small experiment, it turned out Realm is possible to support this kind of relationship with the least amount of effort using LinkingObject.
 * We decided to use Realm for Task Storage. For more details, read To-Many Relationship section in Realm Documentation.
 */
public class TaskStorage {
    private static final String TAG = "TASK_STORAGE";
    private static RealmConfiguration realmConfiguration;

    public TaskStorage(@NonNull Context context) {
        if (realmConfiguration == null) {
            realmConfiguration = new RealmConfiguration.Builder(context)
                    .setModules(new RealmModule())
                    .inMemory()
                    .name("InMemoryTaskStorage")
                    .build();
        }
    }

    public void insertOrUpdateTasks(@NonNull List<Task> tasks, ErrorListener.AdditionErrorListener errorListener) {
        for (Task task : tasks) {
            String unavailableInput = findUnavailableInputs(task.getRequirements());
            if (unavailableInput != null) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "Task with unavailable inputs: " + unavailableInput + ". This should never happen");
                    //TODO: Send Error?
                }
                continue;
            }

            Realm realm = getRealm();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(task);
            realm.commitTransaction();
        }
    }

    @NonNull
    public List<Task> getTasks() {
        Realm realm = getRealm();
        RealmResults<Task> result = realm.allObjects(Task.class);

        List<Task> tasks = new ArrayList<>();
        tasks.addAll(result);
        return tasks;
    }

    @NonNull
    public List<Task> getTasks(@NonNull String byInputName){
        Realm realm = getRealm();
        RealmResults<Task> result = realm.where(Task.class)
                .equalTo("requirements.name", byInputName)
                .findAll();

        List<Task> tasks = new ArrayList<>();
        tasks.addAll(result);
        return tasks;
    }

    @Nullable
    public Task getTask(String byID) {
        Realm realm = getRealm();
        RealmResults<Task> result = realm.where(Task.class).equalTo("id", byID).findAll();

        return (result.size() > 0) ? result.get(0) : null;
    }

    public void deleteTask(@NonNull String byID,
                           @NonNull ErrorListener.DeletionTaskErrorListener errorListener) {
        Task task = getTask(byID);
        if (task == null) {
            errorListener.deletingNonExistingTask();
            return;
        }

        Realm realm = getRealm();
        realm.beginTransaction();
        task.removeFromRealm();
        realm.commitTransaction();
    }

    public void insertOrUpdateAvailableInput(@NonNull String name, int frequency) {
        Input newInput = new Input(name, frequency);

        Realm realm = getRealm();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(newInput);
        realm.commitTransaction();
    }

    public void deleteAvailableInput(@NonNull String byName,
                                     @NonNull ErrorListener.DeletionInputErrorListener errorListener) {
        Input input = getAvailableInput(byName);
        if (input == null) {
            errorListener.deletingUnavailableInput();
            return;
        }

        Realm realm = getRealm();
        realm.beginTransaction();
        input.removeFromRealm();
        realm.commitTransaction();
    }

    @NonNull
    public List<Input> getAvailableInputs() {
        Realm realm = getRealm();
        RealmResults<Input> result = realm.allObjects(Input.class);

        List<Input> inputs = new ArrayList<>();
        inputs.addAll(result);
        return inputs;
    }

    public void insertOrUpdateAvailableAction(String name) {
        Action newAction = new Action(name);

        Realm realm = getRealm();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(newAction);
        realm.commitTransaction();
    }

    public void deleteAvailableAction(@NonNull String byName,
                                      @NonNull ErrorListener.DeletionActionErrorListener errorListener) {
        Action action = getAvailableAction(byName);
        if (action == null) {
            errorListener.deletingUnavailableAction();
            return;
        }

        Realm realm = getRealm();
        realm.beginTransaction();
        action.removeFromRealm();
        realm.commitTransaction();
    }

    @NonNull
    public List<Action> getAvailableActions() {
        Realm realm = getRealm();
        RealmResults result = realm.allObjects(Action.class);

        List<Action> actions = new ArrayList<>();
        actions.addAll(result);
        return actions;
    }

    @Nullable
    private Input getAvailableInput(String byName) {
        Realm realm = getRealm();
        RealmResults<Input> result = realm.where(Input.class).equalTo("name", byName).findAll();
        return (result.size() > 0) ? result.get(0) : null;
    }

    @Nullable
    private Action getAvailableAction(String byName) {
        Realm realm = getRealm();
        RealmResults<Action> result = realm.where(Action.class).equalTo("name", byName).findAll();
        return (result.size() > 0) ? result.get(0) : null;
    }

    @Nullable
    private String findUnavailableInputs(List<Input> requirements) {
        for (Input input : requirements) {
            if (getAvailableInput(input.getName()) == null) {
                return input.getName();
            }
        }
        return null;
    }

    private Realm getRealm() {
        Realm.setDefaultConfiguration(realmConfiguration);
        return Realm.getDefaultInstance();
    }
}
