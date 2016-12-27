package nl.sense_os.iris_android;

import java.util.List;

import nl.sense_os.iris_android.js.executor.JSExecutor;
import nl.sense_os.iris_android.js.executor.JSTask;

/**
 * Created by panjiyudasetya on 12/23/16.
 */

public abstract class Iris {

    public interface Callback {
        void onRequirementsUpdated(List<String> requirements);
        void onActionTriggered(List<String> actionPayload);
    }

    public static void tryJSRuntime() {
        JSExecutor.execute(new JSTask(), "");
    }
}
