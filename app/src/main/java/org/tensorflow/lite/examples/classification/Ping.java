package org.tensorflow.lite.examples.classification;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class Ping extends AppCompatActivity {

    EditText IP1;
    EditText IP2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping);
        IP1 = (EditText) findViewById(R.id.editIP1);
        IP2 = (EditText) findViewById(R.id.editIP2);
        //166.21.100.4

    }

    public void onClickButton(View view)
    {
        if (isConnectedToThisServer(IP1.getText().toString())) {
            Toast.makeText(this, "IP1 Yes, Connected to: "+ IP1.getText().toString() , Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "IP1 No Connection: " + IP1.getText().toString(), Toast.LENGTH_SHORT).show();
        }

        if (isConnectedToThisServer(IP2.getText().toString())) {
            Toast.makeText(this, "IP2 Yes, Connected to: " + IP2.getText().toString(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "IP2 No Connection: " + IP2.getText().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isConnectedToThisServer(String host) {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 " + host);
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
