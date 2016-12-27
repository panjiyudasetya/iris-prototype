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

public class Input extends RealmObject implements TaskConverter {
    @PrimaryKey
    private String name;
    private int frequency;

    public Input() { }

    public Input(String name, int frequency) {
        this.name = name;
        this.frequency = frequency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /**
     * Convert Input into JSON Object if possible.
     * @return JSON Object of Input
     */
    @Override
    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("frequency", frequency);
        } catch (JSONException ex) {
            if (BuildConfig.DEBUG)      ex.printStackTrace();
        }
        return jsonObject;
    }
}
