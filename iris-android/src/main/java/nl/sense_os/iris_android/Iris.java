package nl.sense_os.iris_android;

import android.support.annotation.NonNull;

import org.json.JSONObject;
import nl.sense_os.iris_android.js.executor.JSExecutor;
import nl.sense_os.iris_android.js.executor.JSTask;

/**
 * Created by panjiyudasetya on 12/23/16.
 */

public abstract class Iris {

    public interface Callback {
        void onRequirementsUpdated(@NonNull JSONObject requirements);
        void onActionTriggered(@NonNull JSONObject actionPayload);
    }

    public static void tryJSRuntime() {
        JSExecutor.execute(new JSTask(), "");
    }
}
