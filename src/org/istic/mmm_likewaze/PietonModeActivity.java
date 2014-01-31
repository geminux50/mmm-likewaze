package org.istic.mmm_likewaze;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PietonModeActivity extends FragmentActivity implements LocationListener{
	
	 double latitude;
     double longitude;
     
     private GoogleMap googleMap;
     private CameraUpdate camera;
     private LocationManager locationManager;
     private float zoomFactor = 15f;

     // Current position
     private LatLng currentPosition;

     private ProgressDialog progressDialog;
 	 private List<LatLng> history;
 	 
 	 //bearing
 	 private float bearing;
 	 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pieton_mode);

		try {
			// Initialize the map
			initilizeMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Load all buttons
		final ImageButton btn_zoom_in = (ImageButton) findViewById(R.id.btn_zoom_in_pieton);
		final ImageButton btn_zoom_out = (ImageButton) findViewById(R.id.btn_zoom_out_pieton);
		final Button btn_menu_main = (Button) findViewById(R.id.btn_menu_main_pieton);
		final Button btn_menu_pieton = (Button) findViewById(R.id.btn_menu_pieton);
		
		// Actions for the zoom-in button
		btn_zoom_in.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				camera = CameraUpdateFactory.zoomIn();
				googleMap.moveCamera(camera);
				googleMap.animateCamera(camera);
			}
		});
		
		// Actions for the zoom-out button
		btn_zoom_out.setOnClickListener(new OnClickListener() {
			
			@Override
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
						PietonModeActivity.this, R.string.dialog_main_title,
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
								Intent intent = new Intent(PietonModeActivity.this,
								VehiculeModeActivity.class);
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
		
		
		// Actions for the pieton menu button
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
						final ImageButton btnAutoStop = (ImageButton) childView;
						
						btnAutoStop.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
																
								v.setEnabled(false);
								
								// initialize history array
								history = new ArrayList<LatLng>();
								


								// quantity if Geopoint to be able to calculate bearing
								final int nbOfGeoPts =  2;
								
								AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
									
									@Override
									protected void onPreExecute() {
										
										locationManager.requestLocationUpdates(
												LocationManager.GPS_PROVIDER, 2000, 10, PietonModeActivity.this);
										// Create a Dialog with a progress bar
										progressDialog = new ProgressDialog(PietonModeActivity.this);
										progressDialog.setMax(nbOfGeoPts);
										progressDialog.setTitle("Echantillonage GPS");
										progressDialog.setMessage("Avancer sur une dizaine de mètres dans la direction où vous souhaitez aller");
										progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
										progressDialog.setCancelable(false);
										progressDialog.setIndeterminate(false);
										progressDialog.show();
									}
										
									@Override
									protected Void doInBackground(Void... arg0) {
										try {
											// timeout in milliseconds (+/- threadSleep)
											final int timeout = 20000;
											
											// duration in milliseconds between progress status checks
											final int threadSleep = 2000;
											
											// calculated maximum attemps to perform
											final float attempsMax = timeout/threadSleep;
											
											// attemps counter
											int attempsCnt = 0;
											
											while ((progressDialog.getProgress() < progressDialog.getMax()) && (attempsCnt < attempsMax)) {
												Log.i(this.getClass().getName(),"Check progress:" +progressDialog.getProgress()+ "/" +progressDialog.getMax());
												Thread.sleep(threadSleep);
											}
											
											try {
												bearing = getBearing(history);
												Log.i(PietonModeActivity.class.getName(), String.valueOf(bearing));

											} catch (Exception e) {
												e.printStackTrace();
											}
											
											
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
										return null;
									}
									
									@Override
									protected void onPostExecute(Void result) {
										
										locationManager.requestLocationUpdates(
												LocationManager.GPS_PROVIDER, 5000, 0, PietonModeActivity.this);
										// if the progressdialog has not been manualy closed, do it now
										if (progressDialog!=null) {
											progressDialog.dismiss();
											// and re-enable the autostop button
											btnAutoStop.setEnabled(true);
										}
										
										placementAutoStop(bearing);

									}
										
								};
								task.execute((Void[])null);

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
            //initilizeMap();
    }


	private void panneAction(){
		Toast.makeText(getApplicationContext(), "panneAction",
				Toast.LENGTH_SHORT).show();
		
		LatLng pannePosition = currentPosition;
		
		googleMap.addMarker(new MarkerOptions().
								position(pannePosition).
								icon(BitmapDescriptorFactory.fromResource(R.drawable.panne_96)));
		
		//TODO envoi d'un POI
		
	}

	private void initilizeMap() {

		// Create the map if it does not exists
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map_pieton)).getMap();

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
		//@ModifMarc
		camera = CameraUpdateFactory.newLatLng(currentPosition);
		googleMap.moveCamera(camera);
		googleMap.animateCamera(camera);
		
		// If the progressionDialog is visible (from the autostop button), send
		// it the current location too
		if (progressDialog != null && progressDialog.isShowing()) {
			if (history.size() < progressDialog.getMax()) {
				if (location != null) {
					history.add(new LatLng(location.getLatitude(), location
							.getLongitude()));
					progressDialog.incrementProgressBy(1);
					Log.i(this.getClass().getName(),
							"adding a new position to history");
				}
			}
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
	
	private float getBearing(List<LatLng> gpsPtsList) throws Exception {
		float bearing;
		float[] result = new float[3];

		if (gpsPtsList.size() < 2) {
			throw new Exception("At least 2 LatLng values are required");
		} else {

			LatLng firstGpsPt = gpsPtsList.get(0);
			LatLng lastGpsPt = gpsPtsList.get(gpsPtsList.size() - 1);

			Location.distanceBetween(firstGpsPt.latitude, firstGpsPt.longitude,
					lastGpsPt.latitude, lastGpsPt.longitude, result);

			bearing = result[1];
			Log.i(PietonModeActivity.class.getName(), "Return bearing:" + String.valueOf(bearing));
			return bearing;
		}
	}
	
	
	private void placementAutoStop(float bearing){
		
		if(bearing > 22.5 && bearing < 67.5){
			// Nord Est
			googleMap.addMarker(new MarkerOptions().
					position(currentPosition).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.nordest)));

			//TODO envoi d'un POI
			
		}
		if(bearing > 67.5 && bearing < 112.5){
			// Est
			googleMap.addMarker(new MarkerOptions().
					position(currentPosition).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.est)));

			//TODO envoi d'un POI
			
		}
		if(bearing > 112.5 && bearing < 157.5){
			// Sud Est
			googleMap.addMarker(new MarkerOptions().
					position(currentPosition).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.sudest)));

			//TODO envoi d'un POI
			
		}
		if(bearing > 157.5 && bearing < 202.5){
			// Sud
			googleMap.addMarker(new MarkerOptions().
					position(currentPosition).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.sud)));

			//TODO envoi d'un POI
			
		}
		if(bearing > 202.5 && bearing < 247.5){
			// Sud Ouest
			googleMap.addMarker(new MarkerOptions().
					position(currentPosition).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.sudouest)));

			//TODO envoi d'un POI
			
		}
		if(bearing > 247.5 && bearing < 292.5){
			// Ouest
			googleMap.addMarker(new MarkerOptions().
					position(currentPosition).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.ouest)));

			//TODO envoi d'un POI
			
		}
		if(bearing > 292.5 && bearing < 337.5){
			// Nord Ouest
			googleMap.addMarker(new MarkerOptions().
					position(currentPosition).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.nordouest)));

			//TODO envoi d'un POI
			
		}
		if(bearing > 337.5 || bearing < 22.5){
			// Nord
			googleMap.addMarker(new MarkerOptions().
					position(currentPosition).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.nord)));

			//TODO envoi d'un POI
			
		}

		Toast.makeText(getApplicationContext(), "bearing : " + bearing,
				Toast.LENGTH_LONG).show();
	}
	
	
}
