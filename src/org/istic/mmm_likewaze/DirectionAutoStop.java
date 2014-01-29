package org.istic.mmm_likewaze;

import com.google.android.gms.maps.model.LatLng;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.Toast;

public class DirectionAutoStop extends Activity implements LocationListener {

	private LocationManager locationManager;
	private LatLng[] pts;
	private int nbPts;
	private float cap;
	
	static final int nbPtsUtiles = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_direction_auto_stop);
		
		nbPts = 1;
		
		locationManager = (LocationManager) this
				.getSystemService(LOCATION_SERVICE);

		if (locationManager != null) {
			if (locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				// Refresh the location each 5 secondes (with GPS)
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 2000, 10, this);
			}
			// Refresh the location each 5 secondes (with Network)
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 2000, 10, this);

			// Get the best provider (GPS if enable - best accuracy,
			// Network if GPS is disable)
			String provider = locationManager.getBestProvider(
					new Criteria(), true);

			
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.direction_auto_stop, menu);
		return true;
	}

	@Override
	public void onLocationChanged(Location location) {

		pts[nbPts] = new LatLng(location.getLatitude(),
				location.getLongitude());
		nbPts++;
		
		if(nbPts >= nbPtsUtiles){
			traitementDirection();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	private void traitementDirection(){
		float[] result = new float[3];
		
		Location.distanceBetween(	pts[1].latitude, 
									pts[1].longitude, 
									pts[2].latitude,
									pts[2].longitude,
									result);
		
		
		cap = result[1];
		
		Toast.makeText(getApplicationContext(),
				"cap : " + cap , Toast.LENGTH_LONG)
				.show();
		
		Intent t = new Intent(this, PietonModeActivity.class);
		t.putExtra("Cap", cap);
		t.putExtra("retourDirec", true);
		startActivity(t);
		
		
	}
}
