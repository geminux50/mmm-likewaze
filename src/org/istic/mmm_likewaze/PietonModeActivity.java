package org.istic.mmm_likewaze;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class PietonModeActivity extends FragmentActivity implements LocationListener{
	
	 double latitude;
     double longitude;
     
     private GoogleMap googleMap;
     private CameraUpdate camera;
     private LocationManager locationManager;
     private float zoomFactor = 18f;
     private LatLng currentPosition;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		//********************************************************************
		// verification de la presence d'un accelerometre sur le device !!!
		boolean presenceAccel = GestionAccelerometre.accelerometrePresent(getApplicationContext());
		if(!presenceAccel){
			String texteMsg = "Votre t�l�phone ne possede pas d'acc�l�rom�tre !!!";
			texteMsg += "\n\n";
			texteMsg += "Vous ne pouvez pas utiliser le mode 'PANIC'";
					
			Toast.makeText(getApplicationContext(), texteMsg, Toast.LENGTH_LONG).show();
		}else{
			String texteMsg = "Votre t�l�phone possede un d'acc�l�rom�tre !!!";
			texteMsg += "\n\n";
			texteMsg += "Vous pouvez utiliser le mode 'PANIC'";
					
			Toast.makeText(getApplicationContext(), texteMsg, Toast.LENGTH_LONG).show();
		}
				
		Accelerometre acc = new Accelerometre(this);
		acc.start();
		//********************************************************************
		
		
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pieton_mode);
		
		try {
			initilizeMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		final ImageButton btn_zoom_in = (ImageButton) findViewById(R.id.btn_zoom_in);
		final ImageButton btn_zoom_out = (ImageButton) findViewById(R.id.btn_zoom_out);
		final Button btn_menu_pieton = (Button) findViewById(R.id.btn_menu_pieton);
		
		btn_zoom_in.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				camera = CameraUpdateFactory.zoomIn();
				googleMap.moveCamera(camera);
				googleMap.animateCamera(camera);
			}
		});
		
		btn_zoom_out.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				camera = CameraUpdateFactory.zoomOut();
				googleMap.moveCamera(camera);
				googleMap.animateCamera(camera);
			}
		});
		
		btn_menu_pieton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				MenuDialog menuPieton = new MenuDialog(	PietonModeActivity.this,
														R.string.dialog_pieton_title,
														btn_menu_pieton,
														R.layout.dialog_menu_pieton);
				menuPieton.show();
				
				ViewGroup parentView = (ViewGroup) menuPieton
						.findViewById(R.id.MenuPietonGridLayout);
				for (int i = 0; i < parentView.getChildCount(); i++) {
					View childView = parentView.getChildAt(i);
					int resID = childView.getId();

					switch (resID) {
					
					case R.id.BtnAutoStop:
						
						((ImageButton) childView).setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								autoStopAction();																
							}
						});
						break;

					case R.id.BtnPanne:

						((ImageButton) childView).setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								panneAction();																
							}
						});
						break;
						
					default:
						break;
					}
				}
				
			}
		});
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.vehicule_mode, menu);
		return true;
	}
	
	@Override
    protected void onResume() {
            super.onResume();
            initilizeMap();
    }

	private void autoStopAction(){
		Toast.makeText(getApplicationContext(), "autoStopAction",
				Toast.LENGTH_SHORT).show();
	}
	
	private void panneAction(){
		Toast.makeText(getApplicationContext(), "panneAction",
				Toast.LENGTH_SHORT).show();
	}

	private void initilizeMap() {

		// Create the map if it does not exists
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();

			if (googleMap != null) {
				// Allow the app to get the current position
				googleMap.setMyLocationEnabled(true);

				// Disable built-in zoom buttons
				googleMap.getUiSettings().setZoomControlsEnabled(false);

				// Disable built-in MyLocation button
				googleMap.getUiSettings().setMyLocationButtonEnabled(false);

				// Get the location manager
				locationManager = (LocationManager) this
						.getSystemService(LOCATION_SERVICE);

				if (locationManager != null) {
					if (locationManager
							.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
						// Refresh the location each 5 secondes (with GPS)
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER, 5000, 0, this);
					}
					// Refresh the location each 5 secondes (with Network)
					
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER, 5000, 0, this);

					// Get the best provider (GPS if enable - best accuracy,
					// Network if GPS is disable)
					String provider = locationManager.getBestProvider(
							new Criteria(), true);

					if (provider != null) {
						// Get the last know position even if outdated
						Location location = locationManager
								.getLastKnownLocation(provider);
						if (location != null) {
							LatLng lastPosition = new LatLng(
									location.getLatitude(),
									location.getLongitude());

							// Positionning the camera on the last know position
							// with the given zoom factor
							camera = CameraUpdateFactory.newLatLngZoom(
									lastPosition, zoomFactor);
							googleMap.moveCamera(camera);
							googleMap.animateCamera(camera);
						}
					}
				}
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// Get the current position
		currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

		// Camera follow the new position
		camera = CameraUpdateFactory.newLatLngZoom(currentPosition, zoomFactor);
		googleMap.moveCamera(camera);
		googleMap.animateCamera(camera);
		
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

	
	public void secouage(){
		Toast.makeText(getApplicationContext(), "SECOUER MOI !!!!", Toast.LENGTH_SHORT).show();
	}
	
	
	
}
