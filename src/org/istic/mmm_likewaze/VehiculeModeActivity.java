package org.istic.mmm_likewaze;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class VehiculeModeActivity extends FragmentActivity implements
		LocationListener {

	// Google Map
	private GoogleMap googleMap;
	private LatLng currentPosition = null, lastPosition = null;
	private long currentTime = 0, lastTime = 0;
	private LocationManager locationManager;
	private CameraUpdate camera;
	private float zoomFactor = 18f;
	private double vitesse;
	private AlertDialog msgBox;

	static final int TIME_OUT = 15000;
	static final int MSG_DISMISS_DIALOG = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// verification de la presence d'un accelerometre sur le device !!!
		boolean presenceAccel = GestionAccelerometre
				.accelerometrePresent(getApplicationContext());
		if (!presenceAccel) {
			String texteMsg = "Votre telephone ne possede pas d'accelerometre !!!";
			texteMsg += "\n\n";
			texteMsg += "Vous ne pouvez pas utiliser le mode 'PANIC'";

			Toast.makeText(getApplicationContext(), texteMsg, Toast.LENGTH_LONG)
					.show();
		}

		Accelerometre acc = new Accelerometre(this);
		acc.start();

		setContentView(R.layout.activity_vehicule_mode);
		try {
			// Initialize the map
			initilizeMap();

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
				ViewGroup parentView = (ViewGroup) menuDialogMain
						.findViewById(R.id.MenuMainRelativeLayout);
				for (int i = 0; i < parentView.getChildCount(); i++) {
					View childView = parentView.getChildAt(i);
					int resID = childView.getId();

					switch (resID) {
					case R.id.TvSwitchMode:
						TextView btn = ((TextView) childView);
						btn.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(
										VehiculeModeActivity.this,
										PietonModeActivity.class);
								startActivity(intent);
							}
						});
						break;

					default:
						break;
					}
				}
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
						setPoiBtnAction((ImageButton) childView, PoiType.RADAR);
						break;

					default:
						break;
					}
				}
			}

			private void setPoiBtnAction(ImageButton btn, PoiType poiType) {
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

		createDialog();

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
		float[] result = new float[3];
		lastTime = currentTime;
		currentTime = System.currentTimeMillis();
		long diffTime = (currentTime - lastTime);
        		
		if(currentPosition != null){
			lastPosition = currentPosition;
		}
		
		currentPosition = new LatLng(location.getLatitude(),
				location.getLongitude());

		if(currentPosition != null && lastPosition != null){
			Location.distanceBetween(	lastPosition.latitude, 
										lastPosition.longitude, 
										currentPosition.latitude,
										currentPosition.longitude,
										result);
			
			
			vitesse = (result[0] * 1000 / diffTime)/3.6;
			
			//@todoMarc 
			Toast.makeText(getApplicationContext(), 
					"vitesse = " + vitesse + " km/h", 
					Toast.LENGTH_SHORT).show();
		}
		
		
		
		
		// Camera follow the new position
		//@ModifMarc

		if (currentPosition != null) {
			lastPosition = currentPosition;
		}

		currentPosition = new LatLng(location.getLatitude(),
				location.getLongitude());

		if (currentPosition != null && lastPosition != null) {
			Location.distanceBetween(lastPosition.latitude,
					lastPosition.longitude, currentPosition.latitude,
					currentPosition.longitude, result);

			vitesse = (result[0] * 1000 / diffTime) / 3.6;

			// @todoMarc
			Toast.makeText(getApplicationContext(),
					"vitesse = " + vitesse + " km/h", Toast.LENGTH_SHORT)
					.show();
		}

		// Camera follow the new position
		// @ModifMarc
		camera = CameraUpdateFactory.newLatLng(currentPosition);
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

	public void secouage() {
		// @todoMarc appel � VALID POI
		Toast.makeText(getApplicationContext(), "SECOUER MOI !!!!",
				Toast.LENGTH_SHORT).show();

		msgBox.show();
		mHandler.sendEmptyMessageDelayed(MSG_DISMISS_DIALOG, TIME_OUT);
	}

	private void createDialog() {

		AlertDialog.Builder msgBoxBuilder = new AlertDialog.Builder(this);
		msgBoxBuilder.setMessage("ENVOYER UNE ALERTE ?");
		msgBoxBuilder.setPositiveButton("OUI !!!", null);

		msgBox = msgBoxBuilder.create();

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_DISMISS_DIALOG:
				if (msgBox != null && msgBox.isShowing()) {
					msgBox.dismiss();
				}
				break;

			default:
				break;
			}
		}
	};

}
