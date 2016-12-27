package nl.sense_os.iris_proto;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import nl.sense_os.iris_android.Iris;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Iris.tryJSRuntime();
    }
}