package com.exercise.AndroidClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AndroidClient extends Activity implements SensorEventListener{
        private SensorManager sensorManager;
        public static int totalNoOfCommunication;

        TextView textIn;
        //Accelerometer Data
        TextView xCoor1; // declare X axis object
        TextView yCoor1; // declare Y axis object
        TextView zCoor1; // declare Z axis object
        //Megnetic Field Data
        TextView xCoor2; // declare X axis object
        TextView yCoor2; // declare Y axis object
        TextView zCoor2; // declare Z axis object
        String falldown = "Not Fall";

        @Override
        public void onCreate(Bundle savedInstanceState) {
                 super.onCreate(savedInstanceState);
                 setContentView(R.layout.main);
                 //Registering with respected  TextView
                 xCoor1=(TextView)findViewById(R.id.xcoor1); // create X axis object
                 yCoor1=(TextView)findViewById(R.id.ycoor1); // create Y axis object
                 zCoor1=(TextView)findViewById(R.id.zcoor1); // create Z axis object

                 xCoor2=(TextView)findViewById(R.id.xcoor2); // create X axis object
                 yCoor2=(TextView)findViewById(R.id.ycoor2); // create Y axis object
                 zCoor2=(TextView)findViewById(R.id.zcoor2); // create Z axis object
                 textIn=(TextView)findViewById(R.id.textin);
            //Sensor Registering.
                 sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);

                 sensorManager.registerListener(this,
                         sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                         SensorManager.SENSOR_DELAY_NORMAL);

                 sensorManager.registerListener(this,
                         sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                         SensorManager.SENSOR_DELAY_NORMAL);
                Button buttonSend = (Button)findViewById(R.id.send);
                buttonSend.setOnClickListener(buttonSendOnClickListener);
        }
        public void onSensorChanged(SensorEvent event){
                if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
                    float x=event.values[0];
                    float y=event.values[1];
                    float z=event.values[2];

                    xCoor1.setText("X: "+x);
                    yCoor1.setText("Y: "+y);
                    zCoor1.setText("Z: "+z);
                }
                if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
                    float x=event.values[0];
                    float y=event.values[1];
                    float z=event.values[2];
                    xCoor2.setText("X: "+x);
                    yCoor2.setText("Y: "+y);
                    zCoor2.setText("Z: "+z);
                }
             }
        public void onAccuracyChanged(Sensor sensor,int accuracy){

        }

        Button.OnClickListener buttonSendOnClickListener = new Button.OnClickListener(){

                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    Socket socket = null;
                    DataOutputStream dataOutputStream = null;
                    DataInputStream dataInputStream = null;

                    try {
                        socket = new Socket("192.168.1.6", 5000);
                        NearestNeighbour nn = new NearestNeighbour(3);
                        dataOutputStream = new DataOutputStream(socket.getOutputStream());
                        dataInputStream = new DataInputStream(socket.getInputStream());

                        dataOutputStream.writeUTF("ACCELEROMETER DATA   "+xCoor1.getText().toString());
                        dataOutputStream.writeUTF("                     "+yCoor1.getText().toString());
                        dataOutputStream.writeUTF("                     "+zCoor1.getText().toString());
                        dataOutputStream.writeUTF("MAGNETIC_FIELD DATA  " + xCoor2.getText().toString());
                        //dataOutputStream.writeUTF(xCoor2.getText().toString());
                        dataOutputStream.writeUTF("                     " +yCoor2.getText().toString());
                        dataOutputStream.writeUTF("                     " +zCoor2.getText().toString());
                        textIn.setText(dataInputStream.readUTF());
                        falldown = nn.classify(new NearestNeighbour.DataEntry(new double[]{7,	6,	5,	5,	6,	7},"Ignore")).toString();
                        dataOutputStream.writeUTF("Fall Down (Fall/Not Fall): " + falldown);

                        dataOutputStream.writeUTF("totalNoOfCommuniction : " + totalNoOfCommunication++);
                        //dataOutputStream.flush();
                    } catch (UnknownHostException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } finally{
                            if (socket != null){
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }

                            if (dataOutputStream != null){
                                try {
                                    dataOutputStream.close();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }

                            if (dataInputStream != null){
                                try {
                                    dataInputStream.close();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                    }
                }
        };
}