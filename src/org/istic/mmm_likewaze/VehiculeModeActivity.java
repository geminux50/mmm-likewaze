package org.istic.mmm_likewaze;

import java.util.ArrayList;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.istic.mmm_likewaze.model.Poi;
import com.istic.mmm_likewaze.model.TypePoi;
import com.istic.mmm_likewaze.remote.controller.RemotePoiController;

public class VehiculeModeActivity extends FragmentActivity implements
		LocationListener {

	// Google Map
	private GoogleMap googleMap;
	private LatLng currentPosition;
	private LocationManager locationManager;
	private CameraUpdate camera;
	private float zoomFactor = 13f;
	
	//  Poi service
	
	RemotePoiController  _poicntrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vehicule_mode);
		try {
			// Initialize the map
			initilizeMap();
			// populate teh map with list of Pois 
			populateTheMapWithPoi(googleMap);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Load all buttons
		final ImageButton btn_zoom_in = (ImageButton) findViewById(R.id.btn_zoom_in);
		final ImageButton btn_zoom_out = (ImageButton) findViewById(R.id.btn_zoom_out);
		final Button btn_menu_poi = (Button) findViewById(R.id.btn_menu_poi);
		final Button btn_menu_main = (Button) findViewById(R.id.btn_menu_main);
		final Button btn_menu_call = (Button) findViewById(R.id.btn_menu_call);

		// Actions for the zoom-in button
		btn_zoom_in.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				camera = CameraUpdateFactory.zoomIn();
				googleMap.moveCamera(camera);
				googleMap.animateCamera(camera);
			}
		});

		// Actions for the zoom-out button
		btn_zoom_out.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				camera = CameraUpdateFactory.zoomOut();
				googleMap.moveCamera(camera);
				googleMap.animateCamera(camera);
			}
		});

		// Actions for the main menu button
		btn_menu_main.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				MenuDialog menuDialogMain = new MenuDialog(
						VehiculeModeActivity.this, R.string.dialog_main_title,
						btn_menu_main, R.layout.dialog_menu_main);
				menuDialogMain.show(); 
			}
		});

		// Actions for the POI signalling button
		btn_menu_poi.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				MenuDialog menuDialogPoi = new MenuDialog(
						VehiculeModeActivity.this, R.string.dialog_poi_title,
						btn_menu_poi, R.layout.dialog_menu_poi);
				menuDialogPoi.show();

				ViewGroup parentView = (ViewGroup) menuDialogPoi
						.findViewById(R.id.MenuPoiGridLayout);
				for (int i = 0; i < parentView.getChildCount(); i++) {
					View childView = parentView.getChildAt(i);
					int resID = childView.getId();

					switch (resID) {
					case R.id.BtnRadar:
						setPoiBtnAction((ImageButton) childView, TypePoi.RADAR); 
						break;

					default:
						break;
					}
				}
			}

			private void setPoiBtnAction(ImageButton btn, TypePoi poiType) {
				btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Toast.makeText(getApplicationContext(), "add Marker",
								Toast.LENGTH_SHORT).show();

					}
				});

			}

		});

		// Actions for the emergency call button
		btn_menu_call.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				MenuDialog menuDialogCall = new MenuDialog(
						VehiculeModeActivity.this, R.string.dialog_call_title,
						btn_menu_call, R.layout.dialog_menu_call);
				menuDialogCall.show();

				ViewGroup parentView = (ViewGroup) menuDialogCall
						.findViewById(R.id.MenuCallGridLayout);
				for (int i = 0; i < parentView.getChildCount(); i++) {
					View childView = parentView.getChildAt(i);
					int resID = childView.getId();

					switch (resID) {
					case R.id.BtnCall15:
						placeCall((ImageButton) childView, 15);
						break;
					case R.id.BtnCall17:
						placeCall((ImageButton) childView, 17);
						break;
					case R.id.BtnCall18:
						placeCall((ImageButton) childView, 18);
						break;
					case R.id.BtnCall112:
						placeCall((ImageButton) childView, 112);
						break;

					default:
						break;
					}
				}
			}

			private void placeCall(ImageButton btn, final int phoneNumber) {
				btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO (Toast stub)
						Toast.makeText(getApplicationContext(),
								"Calling " + phoneNumber, Toast.LENGTH_SHORT)
								.show();

					}
				});

			}

		});

	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
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

	
	/**
	 *  Makes a request to the serveur to retrieve alist of POI
	 * @param mMap: the map to populate
	 */
	public void  populateTheMapWithPoi(GoogleMap mMap){
		
		 _poicntrl = new RemotePoiController();
		  ArrayList<Poi>  poiList= _poicntrl.getAllPoi();
		  for(int i=0 ; i< poiList.size(); i++){
			  
			 if( poiList.get(i).getType().equals(TypePoi.ACCIDENT)){
				 mMap.addMarker(new MarkerOptions()
					.position( new LatLng(poiList.get(i).getCurLat(), poiList.get(i).getCurLong())).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.accident)));
				    Log.i("DRWING : "," ACCIDENT  ***********");
			 }
			  
			 if( poiList.get(i).getType().equals(TypePoi.FLOOD)){
				 mMap.addMarker(new MarkerOptions()
					.position( new LatLng(poiList.get(i).getCurLat(), poiList.get(i).getCurLong())).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.waterdrops)));
			 }
			 if( poiList.get(i).getType().equals(TypePoi.POLICE)){
				 mMap.addMarker(new MarkerOptions()
					.position( new LatLng(poiList.get(i).getCurLat(), poiList.get(i).getCurLong())).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.police)));
			 }
			 
			 if( poiList.get(i).getType().equals(TypePoi.USER)){
				 mMap.addMarker(new MarkerOptions()
					.position( new LatLng(poiList.get(i).getCurLat(), poiList.get(i).getCurLong())).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.user)));
			 }
			 
			 if( poiList.get(i).getType().equals(TypePoi.FIRE)){
				 mMap.addMarker(new MarkerOptions()
					.position( new LatLng(poiList.get(i).getCurLat(), poiList.get(i).getCurLong())).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.hazard)));
			 }
			 
			 // so on 
		  }
		
		LatLng position = new LatLng(48.121781, -1.65451);
	      
		mMap.addMarker(new MarkerOptions()
		.position(position).
		icon(BitmapDescriptorFactory.fromResource(R.drawable.accident)));
		  
		  
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
		currentPosition = new LatLng(location.getLatitude(),
				location.getLongitude());

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

}