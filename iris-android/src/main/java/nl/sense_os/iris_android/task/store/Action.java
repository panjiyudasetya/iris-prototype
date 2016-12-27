package nl.sense_os.iris_android.task.store;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.BuildConfig;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import nl.sense_os.iris_android.task.TaskConverter;

/**
 * Created by panjiyudasetya on 12/23/16.
 */

public class Action extends RealmObject implements TaskConverter {
    @PrimaryKey
    private String name;

    public Action() { }

    public Action(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Convert Action into JSON Object if possible.
     * @return JSON Object of The Action
     */
    @Override
    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
        } catch (JSONException ex) {
            if (BuildConfig.DEBUG)      ex.printStackTrace();
        }
        return jsonObject;
    }
}
