package nl.sense_os.iris_android.task.store;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.realm.BuildConfig;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import nl.sense_os.iris_android.task.TaskConverter;

/**
 * Created by panjiyudasetya on 12/23/16.
 */

public class Task extends RealmObject implements TaskConverter {
    @PrimaryKey
    private String id;
    private String desc;
    private String jsCode;
    private String action;
    private RealmList<Input> requirements;

    public Task() { }

    public Task(String id, String desc, String jsCode, String action, RealmList<Input> requirements) {
        this.id = id;
        this.desc = desc;
        this.jsCode = jsCode;
        this.action = action;
        this.requirements = requirements;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getJsCode() {
        return jsCode;
    }

    public void setJsCode(String jsCode) {
        this.jsCode = jsCode;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<Input> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<Input> requirements) {
        this.requirements = new RealmList<>();
        for (int i = 0; i < requirements.size(); i++) {
            this.requirements.add(requirements.get(i));
        }
    }

    /**
     * Convert Task into JSON Object if possible.
     * @return JSON Object of The Task
     */
    @Override
    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("desc", desc);
            jsonObject.put("jsCode", jsCode);
            jsonObject.put("action", action);
            jsonObject.put("requirements", getRequirementsJsonArray());
        } catch (JSONException ex) {
            if (BuildConfig.DEBUG)      ex.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * Get requirements as JSON Array if possible.
     * @return JSON Array of Requirements
     */
    private JSONArray getRequirementsJsonArray() {
        JSONArray jsonArray = new JSONArray();
        for (Input requirement : requirements) {
            jsonArray.put(requirement.toJsonObject());
        }
        return jsonArray;
    }
}