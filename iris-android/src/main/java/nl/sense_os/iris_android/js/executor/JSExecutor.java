package nl.sense_os.iris_android.js.executor;

import android.support.annotation.NonNull;
import android.util.Log;

import com.eclipsesource.v8.V8;
import org.json.JSONObject;

import nl.sense_os.iris_android.BuildConfig;
import nl.sense_os.iris_android.Iris;

/**
 * Created by panjiyudasetya on 12/23/16.
 */

public class JSExecutor {
    private static final String TAG = "[JSExecutor]";
    private Iris.Callback irisCallback;
    private JSLogger jsLogger;


    public static void execute(@NonNull JSTask task, @NonNull String inputs) {
        inputs = "var hello = 'hello, ';\n"
                + "var world = 'world!';\n"
                + "hello.concat(world);\n";
        V8 runtime = V8.createV8Runtime();
        String result = runtime.executeStringScript(inputs);
        System.out.println("--- result : " + result);
        runtime.release();
    }

    public interface JSExecutorErrorListener {
        void incorrectFormatInPayload();
        void payloadIsUnknown();
    }

    public JSExecutor(Iris.Callback irisCallback) {
        this.irisCallback = irisCallback;
        this.jsLogger = new JSLogger();
    }

    // TODO: this should be exposed as a part of public API.
    public void setLogLevel(JSLogger.JSLogLevel logLevel){
        jsLogger.setLogLevel(logLevel);
    }

    public void execute(@NonNull JSTask jsTask, @NonNull JSONObject inputs, @NonNull JSExecutorErrorListener errorListener) {
        // TODO: do we need to clear jsContext? or just instantiate the new?
        V8 jsRuntime = V8.createV8Runtime();

        try {
            // Inject all the inputs to V8 Runtime
            loadInput(jsRuntime, inputs);

            // Exposing java objects to V8 Runtime
            jsRuntime.add("logger", jsLogger.toV8Object(jsRuntime));
            jsRuntime.add("task", jsTask.toV8Object(jsRuntime));

            String jsString = wrapJSCodeInFunction(jsTask.getJsCode());
            Object payload = jsRuntime.executeScript(jsString);

            if (payload == null) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "No payloads returned.");
                    return;
                }
            }

            handlePayload(payload, errorListener);
        } catch (Exception ex) {
            //TODO: Send error report to somewhere?
            if (BuildConfig.DEBUG) {
                Log.e(TAG, String.format("Error on handling Action Payload: %s", ex.getMessage()));
                return;
            }
        } finally {
            jsRuntime.release();
        }
    }

    public void handlePayload(@NonNull Object payload, @NonNull JSExecutorErrorListener errorListener) {
        // TODO: do we need this check?
        if (!(payload instanceof JSONObject)) {
            errorListener.payloadIsUnknown();
        }
        // TODO: do we need to check the format also ?
        boolean doesFormatInPayloadCorrected = true;
        if (!doesFormatInPayloadCorrected) {
            errorListener.incorrectFormatInPayload();
        }

        if (irisCallback != null) {
            irisCallback.onActionTriggered((JSONObject) payload);
        }
    }

    public String wrapJSCodeInFunction(String jsCode) {
        // Wrapping the given JSCode in Task. It encapslate the jsCode inside a function.
        // We need to do this in order to detect when the jsCode is executed until the end of the block.
        // evaluateScript method returns a value at the last line of javascript evaluation, even there is no return statement.
        // Unless we wrap jsCode by a function, it is indistinguishable whether the jsCode was completed until the end or stopped somewhere in the middle of jsCode.
        return "var executeTask = function(){"
                + jsCode
                + "if(typeof payloads !== \"undefined\"){ return payloads; } else {  return null; };"
                + "};"
                + "executeTask();";
    }

    public void loadInput(@NonNull V8 jsRuntime, @NonNull JSONObject inputs) {
        try {
            String script = String.format("var inputs = %s", inputs.toString());
            // Inject inputs as a JSON object.
            // When value is injected one by one as string, then stringified number and number is indistinguishable.
            jsRuntime.executeScript(script);
        } catch (Exception ex) {
            if (BuildConfig.DEBUG) {
                ex.printStackTrace();
            }
        }
    }
}
