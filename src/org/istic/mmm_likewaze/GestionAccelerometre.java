package org.istic.mmm_likewaze;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

public class GestionAccelerometre {
	
	public static boolean accelerometrePresent(Context c){
		
		SensorManager gestionCapteurs = 
				(SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
		Sensor accelerometre = 
				gestionCapteurs.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		
		return (accelerometre != null);
	}

	
	
	
}
