package org.istic.mmm_likewaze;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;



public class Accelerometre implements SensorEventListener{

	public float currenForce;
    private SensorManager sensorManager;
    private List<Sensor> sensors;
    private Sensor sensor;
    private long lastUpdate = -1;
    private long currentTime = -1;
    private float last_x, last_y, last_z;
    private float current_x, current_y, current_z;
    private static final int FORCE_THRESHOLD = 1500;
    
    private final int DATA_X = SensorManager.DATA_X;
    private final int DATA_Y = SensorManager.DATA_Y;
    private final int DATA_Z = SensorManager.DATA_Z;
    
    Activity appelant;
 
    public Accelerometre(Activity parent) {
    	this.appelant = parent;
        SensorManager sensorService = (SensorManager) appelant.getSystemService(Context.SENSOR_SERVICE);
        this.sensorManager = sensorService;
        this.sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            sensor = sensors.get(0);
        }
 
    }
    
    public void start () {
        if (sensor!=null)  {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }
    
    public void stop () {
        sensorManager.unregisterListener(this);
    }
    
    public void onSensorChanged(SensorEvent event) {
 
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER || event.values.length < 3)
            return;
 
        currentTime = System.currentTimeMillis();
 
        if ((currentTime - lastUpdate) > 100) {
            long diffTime = (currentTime - lastUpdate);
            lastUpdate = currentTime;
 
            current_x = event.values[DATA_X];
            current_y = event.values[DATA_Y];
            current_z = event.values[DATA_Z];
 
            currenForce = Math.abs(current_x+current_y+current_z - last_x - last_y - last_z) / diffTime * 10000;
 
 
            if (currenForce > FORCE_THRESHOLD) {
                ((VehiculeModeActivity)appelant).secouage();
            }
            
            last_x = current_x;
            last_y = current_y;
            last_z = current_z;
        }
    }

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}
	

