package nl.sense_os.iris_android.js.executor;

import android.support.annotation.NonNull;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;

/**
 * Created by panjiyudasetya on 12/23/16.
 */

public class JSTaskExports {
    protected double trigger_completed;
    protected double context_completed;

    public double trigger_completed() {
        return trigger_completed;
    }

    public void trigger_completed(double trigger_completed) {
        this.trigger_completed = trigger_completed;
    }

    public double context_completed() {
        return context_completed;
    }

    public void context_completed(double context_completed) {
        this.context_completed = context_completed;
    }

    public V8Object toV8Object(@NonNull V8 jsRuntime) {
        V8Object v8JsTaskExports = new V8Object(jsRuntime);
        v8JsTaskExports.registerJavaMethod(this, "trigger_completed", "trigger_completed", new Class<?>[]{ String.class });
        v8JsTaskExports.registerJavaMethod(this, "context_completed", "context_completed", new Class<?>[]{ String.class });
        return v8JsTaskExports;
    }
}
