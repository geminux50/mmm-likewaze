package org.istic.mmm_likewaze;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.istic.mmm_likewaze.model.Poi;
import org.istic.mmm_likewaze.model.TypePoi;
import org.istic.mmm_likewaze.remote.controller.RemotePoiController;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class VehiculeModeActivity extends FragmentActivity implements
		LocationListener, OnMarkerClickListener {

	// TAG used for logs
	private final static String TAG = VehiculeModeActivity.class.getName();

	// Google Map
	private GoogleMap googleMap;

	// Current Position
	private LatLng currentPosition = null;

	// Last Position (just before the currentPosition)
	private LatLng lastPosition = null;

	// Times for the currentPosition and lastPosition
	private long currentTime = 0, lastTime = 0;

	// Location manager
	private LocationManager locationManager;

	// Camera and its zoom factor
	private CameraUpdate camera;
	private float zoomFactor = 18f;

	// Current measured speed
	private double vitesse;

	// Shaking Dialog and its timeout
	private AlertDialog msgBox;
	private static final int TIME_OUT = 15000;
	private static final int MSG_DISMISS_DIALOG = 0;

	// Speed display
	private TextView tv_vitesse_value;

	// Poi service
	private RemotePoiController _poicntrl = new RemotePoiController();

	// A List of markers (Poi ) which are present the map
	private HashMap<Marker, Long> _liMarkersPois = new HashMap<Marker, Long>();

	// Timer for loading pois
	private Timer _myPoiTimer;

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

			// populate the map with list of POI(s)
			populateTheMapWithPoi(googleMap);

			// set the marker listener

			googleMap.setOnMarkerClickListener(this);

			// Progame the timer to go and load the pois from the server
			_myPoiTimer = new Timer();
			MyTimerTask _mytask = new MyTimerTask();
			_myPoiTimer.schedule(_mytask, 10000, 35000);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Load speed textview
		tv_vitesse_value = (TextView) findViewById(R.id.tv_vitesse_value);

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

			MenuDialog menuDialogMain;

			@Override
			public void onClick(View arg0) {

				menuDialogMain = new MenuDialog(VehiculeModeActivity.this,
						R.string.dialog_main_title, btn_menu_main,
						R.layout.dialog_menu_main);
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
								menuDialogMain.dismiss();
								startActivity(intent);
							}
						});
						break;
					case R.id.DeleteAllPoisId:
						setBtnDeleteAllPoisAction((TextView) childView);
						break;

					case R.id.LoadAllPoisId:
						setBtnLoadAllPoisAction((TextView) childView);
						break;

					default:
						break;
					}
				}
			}

			private void setBtnDeleteAllPoisAction(TextView btn) {

				btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// clear all POIs from server
						clearAllMarkersREQ();
						menuDialogMain.dismiss();

					}
				});

			}

			private void setBtnLoadAllPoisAction(TextView btn) {

				btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// load all POIs from server
						loaddAllPOIREQ();
						menuDialogMain.dismiss();
					}
				});

			}
		});

		// Actions for the POI signaling button
		btn_menu_poi.setOnClickListener(new View.OnClickListener() {

			MenuDialog menuDialogPoi;

			@Override
			public void onClick(View arg0) {

				menuDialogPoi = new MenuDialog(VehiculeModeActivity.this,
						R.string.dialog_poi_title, btn_menu_poi,
						R.layout.dialog_menu_poi);
				menuDialogPoi.show();

				ViewGroup parentView = (ViewGroup) menuDialogPoi
						.findViewById(R.id.MenuPoiGridLayout);

				for (int i = 0; i < parentView.getChildCount(); i++) {
					View childView = parentView.getChildAt(i);
					int resID = childView.getId();

					switch (resID) {

					case R.id.BtnAccident:
						setPoiBtnAction((ImageButton) childView,
								TypePoi.ACCIDENT);
						break;

					case R.id.BtnHazard:
						setPoiBtnAction((ImageButton) childView, TypePoi.DANGER);
						break;

					case R.id.BtnPolice:
						setPoiBtnAction((ImageButton) childView, TypePoi.POLICE);
						break;

					case R.id.BtnSpeedCam:
						setPoiBtnAction((ImageButton) childView, TypePoi.RADAR);
						break;

					case R.id.BtnTrafficJam:
						setPoiBtnAction((ImageButton) childView,
								TypePoi.TRAFFICJAM);
						break;

					case R.id.BtnAquaplaning:
						setPoiBtnAction((ImageButton) childView, TypePoi.FLOOD);
						break;
					default:
						break;
					}
				}
			}

			private void setPoiBtnAction(final ImageButton btn,
					final TypePoi poiType) {
				btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// REQUEST to Server to add this marker
						addThisPoiREQ(getMyCurrentLocation(), poiType);
						menuDialogPoi.dismiss();

					}
				});
			}
		});

		// Actions for the emergency call button
		btn_menu_call.setOnClickListener(new View.OnClickListener() {

			MenuDialog menuDialogCall;

			@Override
			public void onClick(View arg0) {

				menuDialogCall = new MenuDialog(VehiculeModeActivity.this,
						R.string.dialog_call_title, btn_menu_call,
						R.layout.dialog_menu_call);
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
						menuDialogCall.dismiss();
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
	 * Current location
	 * 
	 * @return : current location
	 */
	public LatLng getMyCurrentLocation() {

		LocationManager locationManager = (LocationManager) this
				.getSystemService(LOCATION_SERVICE);
		if (locationManager == null)
			return null;

		String provider = locationManager.getBestProvider(new Criteria(), true);

		if (provider != null) {
			// Get the last know position even if outdated
			Location location = locationManager.getLastKnownLocation(provider);
			if (location != null) {
				LatLng lastPosition = new LatLng(location.getLatitude(),
						location.getLongitude());
				return lastPosition;
			}
		}
		return null;
	}

	private void drawAndSaveMarker(HashMap<Marker, Long> markersMap, Poi poi,
			BitmapDescriptor icon, GoogleMap mMap) {
		Marker m = mMap.addMarker(new MarkerOptions()
				.position(new LatLng(poi.getCurLat(), poi.getCurLong()))
				.icon(icon).title(poi.getLabel()));
		_liMarkersPois.put(m, poi.getIdpoi());
		Log.i(TAG, "Add a poi marker " + poi.getType() + " on the map");
	}

	/**
	 * Makes a request to the serveur to retrieve a list of POI
	 * 
	 * @param mMap
	 *            : the map to populate
	 */
	public void populateTheMapWithPoi(GoogleMap mMap) {

		// clear markers if their are any
		mMap.clear();
		// Reinit list of my poi (s)
		_liMarkersPois = new HashMap<Marker, Long>();
		if (_poicntrl == null)
			_poicntrl = new RemotePoiController();

		ArrayList<Poi> poiList = _poicntrl.getAllPoi();
		for (Poi poi : poiList) {

			TypePoi typePoi = poi.getType();
			BitmapDescriptor icon = null;

			if (typePoi.equals(TypePoi.ACCIDENT)) {
				icon = BitmapDescriptorFactory
						.fromResource(R.drawable.accident_96);
			}

			if (typePoi.equals(TypePoi.RADAR)) {
				icon = BitmapDescriptorFactory
						.fromResource(R.drawable.speedcam_96);
			}

			if (typePoi.equals(TypePoi.FLOOD)) {
				icon = BitmapDescriptorFactory
						.fromResource(R.drawable.aquaplaning_96);
			}

			if (typePoi.equals(TypePoi.POLICE)) {
				icon = BitmapDescriptorFactory
						.fromResource(R.drawable.police_96);
			}

			if (typePoi.equals(TypePoi.USER)) {
				icon = BitmapDescriptorFactory.fromResource(R.drawable.user_96);
			}

			if (typePoi.equals(TypePoi.FIRE)) {
				icon = BitmapDescriptorFactory
						.fromResource(R.drawable.hazard_96);
			}

			if (typePoi.equals(TypePoi.BOUCHON_CALCULE)) {
				icon = BitmapDescriptorFactory
						.fromResource(R.drawable.trafficjam_96);
			}

			if (typePoi.equals(TypePoi.BOUCHON_SIGNALE)) {
				icon = BitmapDescriptorFactory
						.fromResource(R.drawable.trafficjam_96);
			}

			if (typePoi.equals(TypePoi.DANGER)) {
				icon = BitmapDescriptorFactory
						.fromResource(R.drawable.hazard_96);
			}

			if (typePoi.equals(TypePoi.TRAVAUX)) {
				icon = BitmapDescriptorFactory
						.fromResource(R.drawable.unknow_poi_96);
			}

			if (typePoi.equals(TypePoi.PIETON_E)) {
				icon = BitmapDescriptorFactory.fromResource(R.drawable.est);
			}

			if (typePoi.equals(TypePoi.PIETON_N)) {
				icon = BitmapDescriptorFactory.fromResource(R.drawable.nord);
			}

			if (typePoi.equals(TypePoi.PIETON_NE)) {
				icon = BitmapDescriptorFactory.fromResource(R.drawable.nordest);
			}

			if (typePoi.equals(TypePoi.PIETON_NW)) {
				icon = BitmapDescriptorFactory
						.fromResource(R.drawable.nordouest);
			}

			if (typePoi.equals(TypePoi.PIETON_S)) {
				icon = BitmapDescriptorFactory.fromResource(R.drawable.sud);
			}

			if (typePoi.equals(TypePoi.PIETON_SE)) {
				icon = BitmapDescriptorFactory.fromResource(R.drawable.sudest);
			}

			if (typePoi.equals(TypePoi.PIETON_SW)) {
				icon = BitmapDescriptorFactory
						.fromResource(R.drawable.sudouest);
			}

			if (typePoi.equals(TypePoi.PIETON_W)) {
				icon = BitmapDescriptorFactory.fromResource(R.drawable.ouest);
			}

			if (typePoi.equals(TypePoi.NULLTYPE)) {
				icon = BitmapDescriptorFactory
						.fromResource(R.drawable.panne_96);
			}

			if (typePoi.equals(TypePoi.TRAFFICJAM)) {
				icon = BitmapDescriptorFactory
						.fromResource(R.drawable.trafficjam_96);
			}

			if (icon != null) {
				drawAndSaveMarker(_liMarkersPois, poi, icon, mMap);
			}
		}

	}

	@Override
	public boolean onMarkerClick(final Marker marker) {

		if (_liMarkersPois.containsKey(marker)) {
			Log.i(TAG, "Marker found : ," + _liMarkersPois.get(marker)
					+ " please wait ..");
			clearAPoiREQ(_liMarkersPois.get(marker));
		} else {

		}

		return true;
	}

	/**
	 * Request to the server to add this poi
	 * 
	 * @param pos
	 *            : position of the poi to be added
	 * @param type
	 *            : type of the poi to add
	 */
	public void addThisPoiREQ(LatLng pos, TypePoi type) {
		Log.i(TAG, "add Marker: " + type + " ,  "
				+ getMyCurrentLocation().latitude + " - "
				+ getMyCurrentLocation().longitude);
		Poi po = new Poi();
		po.setCurLat(pos.latitude);
		po.setCurLong(pos.longitude);
		po.setType(type);
		po.setLabel(type + "  label");
		// call the service to add poi
		_poicntrl.addPoi(po);
		// reaload the map from server
		populateTheMapWithPoi(googleMap);
	}

	/**
	 * Clear a poi from the map
	 * 
	 * @param poid
	 *            : the poi identifier
	 */
	public void clearAPoiREQ(final Long poid) {

		AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
		builder1.setMessage(" Supprimer ce POI: " + poid + "?");
		builder1.setCancelable(true);
		builder1.setPositiveButton("Oui",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						// MAKE A CALL TO THE POI SERVICE TO DELETE THIS POI
						Long idPoiToDelete = poid;
						_poicntrl.deletePoi(idPoiToDelete);
						Toast.makeText(getApplicationContext(),
								"Le POI a bien été supprimé",
								Toast.LENGTH_SHORT).show();
						googleMap.clear();
						populateTheMapWithPoi(googleMap);
					}
				});
		builder1.setNegativeButton("Non",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		AlertDialog alert11 = builder1.create();
		alert11.show();

	}

	/**
	 * Clear the markers from the map. this has a local effect To delete a
	 * spacific marker (poi) for all the community right click on the marker ...
	 * you will be guided don't worry ^^
	 */
	public void clearAllMarkersREQ() {
		AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
		builder1.setMessage("   Supprimer tous les POIs ?");
		builder1.setCancelable(true);
		builder1.setPositiveButton("Oui",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Log.i(TAG, "All POIs have been deleted");
						Toast.makeText(getApplicationContext(),
								" Suppression de tous les POIs (En local)",
								Toast.LENGTH_LONG).show();
						dialog.cancel();
						googleMap.clear();
					}
				});
		builder1.setNegativeButton("Non",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		AlertDialog alert11 = builder1.create();
		alert11.show();
	}

	/**
	 * Request to the server to download all pois
	 */
	public void loaddAllPOIREQ() {
		AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
		builder1.setMessage("  Télécharger tous les POIs ?");
		builder1.setCancelable(true);
		builder1.setPositiveButton("oui",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Log.i(TAG, "All POIs have been refreshed");

						Toast.makeText(getApplicationContext(),
								"Tous les POIs ont été téléchargés",
								Toast.LENGTH_LONG).show();
						dialog.cancel();
						googleMap.clear();
						populateTheMapWithPoi(googleMap);
					}
				});
		builder1.setNegativeButton("Non",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();

					}
				});

		AlertDialog alert11 = builder1.create();
		alert11.show();
	}

	@Override
	public void onLocationChanged(Location location) {
		// Get the current position

		float[] result = new float[3];
		lastTime = currentTime;
		currentTime = System.currentTimeMillis();
		long diffTime = (currentTime - lastTime);

		if (currentPosition != null) {
			lastPosition = currentPosition;
		}

		currentPosition = new LatLng(location.getLatitude(),
				location.getLongitude());

		if (currentPosition != null && lastPosition != null) {
			Location.distanceBetween(lastPosition.latitude,
					lastPosition.longitude, currentPosition.latitude,
					currentPosition.longitude, result);

			vitesse = Math.round((result[0] * 1000 / diffTime) / 3.6);

			if (tv_vitesse_value != null) {
				tv_vitesse_value.setText(String.valueOf(vitesse));
			}
		}

		// Camera follow the new position
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
		// Only one AlertBox at the same time
		if (msgBox == null || (msgBox != null && !msgBox.isShowing())) {
			AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
			builder1.setMessage(" Signaler un DANGER ?");
			builder1.setCancelable(true);
			builder1.setPositiveButton("Oui",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							addThisPoiREQ(getMyCurrentLocation(),
									TypePoi.DANGER);
							dialog.cancel();
						}
					});
			builder1.setNegativeButton("Non",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();

						}
					});

			msgBox = builder1.create();
			msgBox.show();
			mHandler.sendEmptyMessageDelayed(MSG_DISMISS_DIALOG, TIME_OUT);
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle mysaver) {
		super.onSaveInstanceState(mysaver);
		// save muy location
		/*
		 * mysaver.putLong("MyLocationPoi",_myLocationPoi);
		 */

	}

	/**
	 * Reload all the POI (s) from the server at a given interval time
	 * 
	 * @author me
	 * 
	 */
	class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Log.i(TAG, "Reloding of the POI (s)  ... ");
					populateTheMapWithPoi(googleMap);
				};
			});

		}
	}

	@SuppressLint("HandlerLeak")
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

	@Override
	protected void onResume() {
		super.onResume();
		// Reinitializing the map
		initilizeMap();

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (locationManager != null) {
			locationManager.removeUpdates(this);
		}
	}

	@Override
	protected void onStop() {
		super.onPause();
		if (locationManager != null) {
			locationManager.removeUpdates(this);
		}
	}

}
