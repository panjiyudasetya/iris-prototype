package nl.sense_os.iris_android.js.executor;

import android.support.annotation.NonNull;

import com.eclipsesource.v8.V8;

/**
 * Created by panjiyudasetya on 12/23/16.
 */

public class JSExecutor {

    public static void execute(@NonNull JSTask task, @NonNull String inputs) {
        inputs = "var hello = 'hello, ';\n"
                + "var world = 'world!';\n"
                + "hello.concat(world);\n";
        V8 runtime = V8.createV8Runtime();
        String result = runtime.executeStringScript(inputs);
        System.out.println("--- result : " + result);
        runtime.release();
    }
}
