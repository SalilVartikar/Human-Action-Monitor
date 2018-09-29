package com.example.jadhosn.group9_a3;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button record, visualization, train, test;
    RadioButton walking, running, jumping;
    DatabaseHelper myDb;
    ProgressDialog bubble;

    private int activity_label = 4;

    private int counter = 0;

    //Sensor Code
    private String row = "";
    long prevTime = 0;
    int columns = 0;
    private float x,y,z;
    SensorManager manager;
    Sensor accelerometer;

    SensorEventListener listener = new SensorEventListener()
    {
        public void onAccuracyChanged(Sensor sensor, int accuracy){}
        public void onSensorChanged(SensorEvent event)
        {
            long currentTime = System.currentTimeMillis();
            Sensor mySensor = event.sensor;
            if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER)
            {
                x=event.values[0];
                y=event.values[1];
                z=event.values[2];

                if((currentTime - prevTime)>100)
                {
                    String data = String.valueOf(x) + "," + String.valueOf(y) + "," + String.valueOf(z);
                        if (columns < 50) {
                            row = row + "," + data;
                            prevTime = currentTime;
                            columns++;

                        }
                        if (columns == 50) {
                            counter++;
                            row = String.valueOf(currentTime) + row + "," + String.valueOf(activity_label);
                            Log.d("data", row);
                            myDb.insertRow(row);
                            row = "";
                            columns = 0;
                            bubble.dismiss();
                            onStop();
                        }
                        //Enable this for the final submission
                    /*
                        if(counter>60){counter=0;
                            train.setEnabled(true);}
                            */
                }
            }
        }
    };
    public void registerSensor()
    {   manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        manager.registerListener(listener,accelerometer, 10);
    }
    protected void onStop() {
        manager.unregisterListener(listener);
        super.onStop();
    }
    //End of sensor code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
                return;
            }
        }

        myDb = new DatabaseHelper(this);

        record = (Button) findViewById(R.id.record);
        record.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bubble = ProgressDialog.show(MainActivity.this, "Recording data", "Keep "+whichActivty(activity_label)+" ...");
                registerSensor();
            }
        });

        visualization = (Button) findViewById(R.id.visualization);
        visualization.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                registerSensor();
                onStop();
                Intent intent = new Intent(MainActivity.this,Visualization.class);
                startActivity(intent);


            }
       });

        test = (Button) findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Train.class);
                intent.putExtra("testing",true);
                startActivity(intent);
            }
        });


        train = (Button) findViewById(R.id.train);
        //Salil enable this for the final submission
        //train.setEnabled(false);
        train.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                registerSensor();
                onStop();
                /*
                try {
                    converter();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */
                Intent intent = new Intent(MainActivity.this,Train.class);
                intent.putExtra("testing",false);
                startActivity(intent);
            }
        });


    }

    public void converter() throws IOException
    {
        Cursor result = myDb.getAllData();
        File filewrite = new File(Environment.getExternalStorageDirectory() + "/Android/Data/CSE535_ASSIGNMENT3","group9.txt");
        FileWriter writer=new FileWriter(filewrite);

        String output = "";
        String data = "";

        if(result.moveToFirst()) {
            String[] columns = result.getColumnNames();
            columns = Arrays.copyOfRange(columns,1,151);
            String label = "";
            do{
                int count=1;
                for(String name: columns)
                {
                        data+= String.format("%s:%s ",String.valueOf(count),result.getString(result.getColumnIndex(name)));
                        count+=1;

                }
                label = result.getString(151);
                output += label +" "+data+ "\n";
                label="";
            }while(result.moveToNext());
        }
        Log.d("",output);
        result.close();
        writer.append(output);
        writer.flush();
        writer.close();
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.Walking:
                if (checked)
                    activity_label = 1;
                break;
            case R.id.Running:
                if (checked)
                    activity_label = 2;
                break;
            case R.id.Jumping:
                if (checked)
                    activity_label = 3;
                break;
        }
    }

    public String whichActivty(int i)
    {
        String Activity="";
        if(i==1)Activity="Walking";
        if(i==2)Activity="Running";
        if(i==3)Activity="Jumping";
        return Activity;
    }

}
