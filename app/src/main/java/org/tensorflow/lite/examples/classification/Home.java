package org.tensorflow.lite.examples.classification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Home extends AppCompatActivity {

    private Button missionStart;
    private Button sensorData;
    private Button ping;
    private Button maps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        missionStart = (Button) findViewById(R.id.btnMissionStart);
        missionStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMission();
            }
        });

        sensorData = (Button) findViewById(R.id.btnSensorData);
        sensorData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSensor();
            }
        });

        ping = (Button) findViewById(R.id.btnPing);
        ping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pingDevice();
            }
        });

//        maps = (Button) findViewById(R.id.btnGPS);
//        maps.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openMap();
//            }
//        });
    }

    public void openMission(){
        Intent intent = new Intent(this, ClassifierActivity.class);
        startActivity(intent);
    }

    public void openSensor(){
        Intent intent = new Intent(this, SensorData.class);
        startActivity(intent);
    }

    public void pingDevice(){
        Intent intent = new Intent(this, Ping.class);
        startActivity(intent);
    }

    public void openMap(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
