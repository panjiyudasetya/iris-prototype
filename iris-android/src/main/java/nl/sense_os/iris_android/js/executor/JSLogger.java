package nl.sense_os.iris_android.js.executor;

import android.support.annotation.NonNull;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;

import io.realm.BuildConfig;

/**
 * Created by panjiyudasetya on 12/23/16.
 */

public class JSLogger {
    private JSLogLevel desiredLogLevel = JSLogLevel.ERROR;

    enum JSLogLevel {
        NO_LOG, ERROR, WARNING, INFO, DEBUG, VERBOSE
    }

    public V8Object toV8Object(@NonNull V8 jsRuntime) {
        V8Object v8Logger = new V8Object(jsRuntime);
        v8Logger.registerJavaMethod(this, "error", "error", new Class<?>[] {String.class});
        v8Logger.registerJavaMethod(this, "warning", "warning", new Class<?>[] {String.class});
        v8Logger.registerJavaMethod(this, "info", "info", new Class<?>[] {String.class});
        v8Logger.registerJavaMethod(this, "debug", "debug", new Class<?>[] {String.class});
        v8Logger.registerJavaMethod(this, "verbose", "verbose", new Class<?>[] {String.class});
        return v8Logger;
    }

    public void setLogLevel(JSLogLevel logLevel) {
        desiredLogLevel = logLevel;
    }

    public void error(String text) {
        printForLogLevel(text, JSLogLevel.ERROR);
    }

    public void warning(String text) {
        printForLogLevel(text, JSLogLevel.WARNING);
    }

    public void info(String text) {
        printForLogLevel(text, JSLogLevel.INFO);
    }

    public void debug(String text) {
        printForLogLevel(text, JSLogLevel.DEBUG);
    }

    public void verbose(String text) {
        printForLogLevel(text, JSLogLevel.VERBOSE);
    }

    private void printForLogLevel(String text, JSLogLevel logLevel){
        if (BuildConfig.DEBUG) {
            System.out.println(String.format("[JSLOG] %s - %s", logLevel.name(), text));
        }
    }
}
