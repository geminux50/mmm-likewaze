package org.istic.mmm_likewaze;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class VehiculeModeActivity extends FragmentActivity implements
		LocationListener {

	// Google Map
	private GoogleMap googleMap;
	private LatLng currentPosition;
	private LocationManager locationManager;
	private CameraUpdate camera;
	private float zoomFactor = 18f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vehicule_mode);
		try {
			// Initialize the map
			initilizeMap();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Custom zoom boutons
		final ImageButton btn_zoom_in = (ImageButton) findViewById(R.id.btn_zoom_in);
		final ImageButton btn_zoom_out = (ImageButton) findViewById(R.id.btn_zoom_out);

		btn_zoom_in.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	camera = CameraUpdateFactory.zoomIn();
            	googleMap.moveCamera(camera);
            	googleMap.animateCamera(camera);
            }
        });
		
		btn_zoom_out.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	camera = CameraUpdateFactory.zoomOut();
            	googleMap.moveCamera(camera);
            	googleMap.animateCamera(camera);
            }
        });

	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initilizeMap() {

		// Create the map if it does not exists
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

			if (googleMap != null) {
				// Allow the app to get the current position
				googleMap.setMyLocationEnabled(true);
				
				// Disable built-in zoom buttons
				googleMap.getUiSettings().setZoomControlsEnabled(false);
				
				// Disable built-in MyLocation button
				googleMap.getUiSettings().setMyLocationButtonEnabled(false);

				
				// Get the location manager
				locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
				
				if (locationManager != null) {
					if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
						// Refresh the location each 5 secondes (with GPS)
						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
					}
					// Refresh the location each 5 secondes (with Network)
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
					
					// Get the best provider (GPS if enable - best accuracy, Network if GPS is disable)
					String provider = locationManager.getBestProvider(new Criteria(), true);
						
					if (provider != null) {
						// Get the last know position even if outdated
						Location location = locationManager.getLastKnownLocation(provider);
						if (location != null) {
							LatLng lastPosition = new LatLng(location.getLatitude(), location.getLongitude());
							
							//Positionning the camera on the last know position with the given zoom factor
							camera = CameraUpdateFactory.newLatLngZoom(lastPosition, zoomFactor);
							googleMap.moveCamera(camera);
							googleMap.animateCamera(camera);
						}
					}		
				}
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Reinitializing the map
		initilizeMap();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.vehicule_mode, menu);
		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
		// Get the current position
		currentPosition = new LatLng(location.getLatitude(),location.getLongitude());
		
		// Positionning the camera on the last know position with the given zoom factor
		camera = CameraUpdateFactory.newLatLngZoom(currentPosition, zoomFactor);
//		googleMap.moveCamera(camera);
//		googleMap.animateCamera(camera);
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

}
