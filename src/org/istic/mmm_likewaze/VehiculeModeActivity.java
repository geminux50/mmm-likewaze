package org.istic.mmm_likewaze;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import junit.framework.Test;

import org.istic.mmm_likewaze.model.Poi;
import org.istic.mmm_likewaze.model.TypePoi;
import org.istic.mmm_likewaze.remote.controller.RemotePoiController;

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
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class VehiculeModeActivity extends FragmentActivity implements
		LocationListener, OnMarkerClickListener {

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

	//  Poi service
	RemotePoiController  _poicntrl = new RemotePoiController();
  
	private Long _myLocationPoi;
	
	//  A List of markers (Poi ) which  are present  the map 
	private HashMap<Marker, Long> _liMarkersPois = new HashMap<Marker, Long>();
	
	
	//  Timer for loading pois 
	 Timer _myPoiTimer;
			 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		
		
		
		//  Try to get the saved data
		/*if( savedInstanceState != null){
			
			_myLocationPoi = (Long)savedInstanceState.get("MyLocationPoi");
			
			}
		}*/
		
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
			 MyTimerTask _mytask= new MyTimerTask(); 
			_myPoiTimer.schedule(_mytask,10000,35000);
			
			
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
						
						Toast.makeText(getApplicationContext(), " delete all pois actions",
								Toast.LENGTH_SHORT).show();
						
						clearAllMarkersREQ();
						

					}
				});

			}
			
			private void setBtnLoadAllPoisAction(TextView btn) {

				btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						
						Toast.makeText(getApplicationContext(), " load all pois actions",
								Toast.LENGTH_SHORT).show();
						
						loaddAllPOIREQ();  // load all pois from server
						

					}
				});

			}
		});

		// Actions for the POI signaling button
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
					
					/*case R.id.BtnRadar:
						setPoiBtnAction((ImageButton) childView,TypePoi.RADAR);
						Toast.makeText(getApplicationContext(), "add Marker:" +TypePoi.RADAR,
								Toast.LENGTH_SHORT).show();
						break;*/

					case R.id.BtnAccident:
						
						setPoiBtnAccidentAction((ImageButton) childView);
						
						break;
					case R.id.Btnhazard:
						
						setPoiBtnHazardAction((ImageButton) childView);
						
						break;
					case R.id.BtnPolice:
						
						setPoiBtnPoliceAction((ImageButton) childView);
						
						break;
					case R.id.BtnSpeedCam:
						
						setPoiBtnSpeedCamAction((ImageButton) childView);
						
						break;	
						
					case R.id.BtntrafficJam:
						
						setPoiBtnTrafficJamAction((ImageButton) childView);
						break;	
                		
								
					default:
						break;
					}
				}
			}

			private void setPoiBtnAccidentAction(ImageButton btn) {

				btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						
                           // REQUEST to Server to add this marker
						    addThisPoiREQ(getMyCurrentLocation(),TypePoi.ACCIDENT );
						
					}
				});

			}
			
			private void setPoiBtnHazardAction(ImageButton btn) {

				btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
				
						// REQUEST to Server to add this marker
						addThisPoiREQ(getMyCurrentLocation(),TypePoi.DANGER );
					}
				});

			}
			private void setPoiBtnPoliceAction(ImageButton btn) {

				btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						
						
						// REQUEST to Server to add this marker
						addThisPoiREQ(getMyCurrentLocation(),TypePoi.POLICE);
					}
				});

			}
			private void setPoiBtnTrafficJamAction(ImageButton btn) {

				btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						
						// REQUEST to Server to add this marker
						addThisPoiREQ(getMyCurrentLocation(),TypePoi.TRAFFICJAM );
						
					}
				});

			}
			private void setPoiBtnSpeedCamAction(ImageButton btn) {

				btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						
						
						// REQUEST to Server to add this marker
						addThisPoiREQ(getMyCurrentLocation(),TypePoi.RADAR );
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

	
	/**
	 *  Current location 
	 * @return    : current location
	 */
	public LatLng getMyCurrentLocation(){
		
		LocationManager locationManager = (LocationManager) this
				.getSystemService(LOCATION_SERVICE);
		if(locationManager ==null)   return  null;
		
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
				return lastPosition;
			}
		}
	   return null;		
	}
	
	/**
	 *  Makes a request to the serveur to retrieve a list of POI
	 * @param mMap: the map to populate
	 */
	public void  populateTheMapWithPoi(GoogleMap mMap){
		
		 googleMap.clear();  // clear markers if their are any 
		 // Reinit list of my poi (s)
		 _liMarkersPois = new HashMap<Marker, Long>();
		 if(_poicntrl ==null)
		          _poicntrl = new RemotePoiController();
		 
		 //addMyPoi();
		 
		 ArrayList<Poi>  poiList= _poicntrl.getAllPoi();
		  for(int i=0 ; i< poiList.size(); i++){
			  
			 if( poiList.get(i).getType().equals(TypePoi.ACCIDENT)){
				Marker m = mMap.addMarker(new MarkerOptions()
					.position( new LatLng(poiList.get(i).getCurLat(), poiList.get(i).getCurLong())).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.accident))
					.title(poiList.get(i).getLabel())
					);
				_liMarkersPois.put(m, poiList.get(i).getIdpoi());
				    Log.i("DRWING : "," ACCIDENT  ***********");
			 }
			 if( poiList.get(i).getType().equals(TypePoi.RADAR)){
				 Marker m = mMap.addMarker(new MarkerOptions()
					.position( new LatLng(poiList.get(i).getCurLat(), poiList.get(i).getCurLong())).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.speedcam))
					.title(poiList.get(i).getLabel())
					);
				 _liMarkersPois.put(m, poiList.get(i).getIdpoi());
				    Log.i("DRWING : "," ACCIDENT  ***********");
			 }
			  
			 if( poiList.get(i).getType().equals(TypePoi.FLOOD)){
				 Marker m = mMap.addMarker(new MarkerOptions()
					.position( new LatLng(poiList.get(i).getCurLat(), poiList.get(i).getCurLong())).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.waterdrops))
					.title(poiList.get(i).getLabel())
					);
				 _liMarkersPois.put(m, poiList.get(i).getIdpoi());
			 }
			 if( poiList.get(i).getType().equals(TypePoi.POLICE)){
				 Marker m = mMap.addMarker(new MarkerOptions()
					.position( new LatLng(poiList.get(i).getCurLat(), poiList.get(i).getCurLong())).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.police))
					.title(poiList.get(i).getLabel())
					
					);
				 _liMarkersPois.put(m, poiList.get(i).getIdpoi());
				 
			 }
			 
			 if( poiList.get(i).getType().equals(TypePoi.USER)){
				 Marker m = mMap.addMarker(new MarkerOptions()
					.position( new LatLng(poiList.get(i).getCurLat(), poiList.get(i).getCurLong())).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.user))
					.title(poiList.get(i).getLabel())
					);
				 _liMarkersPois.put(m, poiList.get(i).getIdpoi());
			 }
			 
			 if( poiList.get(i).getType().equals(TypePoi.FIRE)){
				 Marker m = mMap.addMarker(new MarkerOptions()
					.position( new LatLng(poiList.get(i).getCurLat(), poiList.get(i).getCurLong())).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.hazard))
				.title(poiList.get(i).getLabel())	
				);
				 _liMarkersPois.put(m, poiList.get(i).getIdpoi());
			 }
			 
			 if( poiList.get(i).getType().equals(TypePoi.BOUCHON_CALCULE)){
				 Marker m = mMap.addMarker(new MarkerOptions()
					.position( new LatLng(poiList.get(i).getCurLat(), poiList.get(i).getCurLong())).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.trafficjam))
				.title(poiList.get(i).getLabel())	
				);  
				 _liMarkersPois.put(m, poiList.get(i).getIdpoi());
			 }
             if( poiList.get(i).getType().equals(TypePoi.BOUCHON_SIGNALE)){
            	 Marker m =mMap.addMarker(new MarkerOptions()
					.position( new LatLng(poiList.get(i).getCurLat(), poiList.get(i).getCurLong())).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.trafficjam))
				.title(poiList.get(i).getLabel())	
            	);  
            	 _liMarkersPois.put(m, poiList.get(i).getIdpoi());
			 }
              
             if( poiList.get(i).getType().equals(TypePoi.DANGER)){
            	 Marker m =mMap.addMarker(new MarkerOptions()
					.position( new LatLng(poiList.get(i).getCurLat(), poiList.get(i).getCurLong())).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.hazard))
					.title(poiList.get(i).getLabel())
            		); 
            	 _liMarkersPois.put(m, poiList.get(i).getIdpoi());
 			 }
             
             if( poiList.get(i).getType().equals(TypePoi.TRAVAUX)){
 				 
 			 }
             if( poiList.get(i).getType().equals(TypePoi.PIETON_E)){
            	 Marker m = mMap.addMarker(new MarkerOptions()
					.position( new LatLng(poiList.get(i).getCurLat(), poiList.get(i).getCurLong())).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.est))
					.title(poiList.get(i).getLabel())
            		);
            	 _liMarkersPois.put(m, poiList.get(i).getIdpoi());
 			 }
             
             if( poiList.get(i).getType().equals(TypePoi.PIETON_N)){
            	 Marker m = mMap.addMarker(new MarkerOptions()
					.position( new LatLng(poiList.get(i).getCurLat(), poiList.get(i).getCurLong())).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.nord))
					.title(poiList.get(i).getLabel())
            		);
            	 _liMarkersPois.put(m, poiList.get(i).getIdpoi());
 			 }
             if( poiList.get(i).getType().equals(TypePoi.PIETON_NE)){
            	 Marker m = mMap.addMarker(new MarkerOptions()
					.position( new LatLng(poiList.get(i).getCurLat(), poiList.get(i).getCurLong())).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.nordest))
				.title(poiList.get(i).getLabel())
            	); 
            	 _liMarkersPois.put(m, poiList.get(i).getIdpoi());
 			 }
			
             if( poiList.get(i).getType().equals(TypePoi.PIETON_NW)){
            	 Marker m = mMap.addMarker(new MarkerOptions()
					.position( new LatLng(poiList.get(i).getCurLat(), poiList.get(i).getCurLong())).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.nordouest))	
            	   .title(poiList.get(i).getLabel())		 
            	);
            	 _liMarkersPois.put(m, poiList.get(i).getIdpoi());
 			 }
			 if( poiList.get(i).getType().equals(TypePoi.PIETON_S)){
				 Marker m = mMap.addMarker(new MarkerOptions()
					.position( new LatLng(poiList.get(i).getCurLat(), poiList.get(i).getCurLong())).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.sud))
				  .title(poiList.get(i).getLabel())	
				); 
				 _liMarkersPois.put(m, poiList.get(i).getIdpoi());
			  }
						
		     if( poiList.get(i).getType().equals(TypePoi.PIETON_SE)){
		    	 Marker m =mMap.addMarker(new MarkerOptions()
					.position( new LatLng(poiList.get(i).getCurLat(), poiList.get(i).getCurLong())).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.sudest)));
		    	 _liMarkersPois.put(m, poiList.get(i).getIdpoi());
			 }
			 if( poiList.get(i).getType().equals(TypePoi.PIETON_SW)){
				 
				 Marker m = mMap.addMarker(new MarkerOptions()
					.position( new LatLng(poiList.get(i).getCurLat(), poiList.get(i).getCurLong())).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.sudouest))
					.title(poiList.get(i).getLabel())
					);
				 
				 _liMarkersPois.put(m, poiList.get(i).getIdpoi());
			 }
			 if( poiList.get(i).getType().equals(TypePoi.PIETON_W)){
				 Marker m = mMap.addMarker(new MarkerOptions()
					.position( new LatLng(poiList.get(i).getCurLat(), poiList.get(i).getCurLong())).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.ouest))
				  .title(poiList.get(i).getLabel())	
				);
				 _liMarkersPois.put(m, poiList.get(i).getIdpoi());
			  }
			 
			 if( poiList.get(i).getType().equals(TypePoi.NULLTYPE)){  // ^^ replace it with PANNE TYPE
				 Marker m = mMap.addMarker(new MarkerOptions()
					.position( new LatLng(poiList.get(i).getCurLat(), poiList.get(i).getCurLong())).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.panne_96))
				  .title(poiList.get(i).getLabel())	
				);
				 _liMarkersPois.put(m, poiList.get(i).getIdpoi());
			  }
			 // TRAFFICJAM
			 if( poiList.get(i).getType().equals(TypePoi.TRAFFICJAM)){  // ^^ replace it with PANNE TYPE
				 Marker m = mMap.addMarker(new MarkerOptions()
					.position( new LatLng(poiList.get(i).getCurLat(), poiList.get(i).getCurLong())).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.trafficjam))
				  .title(poiList.get(i).getLabel())	
				);
				 _liMarkersPois.put(m, poiList.get(i).getIdpoi());
			  }
			 // so on 
		  }
		
		/*LatLng position = new LatLng(48.121781, -1.65451);
	      
		mMap.addMarker(new MarkerOptions()
		.position(position).
		icon(BitmapDescriptorFactory.fromResource(R.drawable.accident)));*/
		  
		  
	}
	
	@Override
	public boolean onMarkerClick(final Marker marker){
		
		
		if(_liMarkersPois.containsKey(marker)){
			
			Toast.makeText(getApplicationContext(),
					"Marker found : ,"+_liMarkersPois.get(marker) +" please wait ..", Toast.LENGTH_LONG)
					.show();
			clearAPoiREQ(_liMarkersPois.get(marker));
		}else{
			
		}
		
		return true;
	}
	
	/**
	 *   Request to the server to add this poi
	 * @param pos  : position of the poi to be added 
	 * @param type : type of the poi to add
	 */
	public void addThisPoiREQ(LatLng pos,TypePoi type ){
		
		Toast.makeText(getApplicationContext(), "add Marker: "+type+" ,  "+
				getMyCurrentLocation().latitude+" - "+getMyCurrentLocation().longitude,
				Toast.LENGTH_SHORT).show();
		
		Poi po = new Poi();
		po.setCurLat(pos.latitude);po.setCurLong(pos.longitude);
		po.setType(type);
		po.setLabel(type+"  label");
		// call the service to add poi
		_poicntrl.addPoi(po);
		// reaload the map from server
		populateTheMapWithPoi(googleMap);
	}
	
	/**
	 * Clear a poi from the map
	 * 
	 * @param poid : the poi identifier 
	 */
	public void clearAPoiREQ(final Long poid){
		
		
		AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(" Do you want to delete this POI s "+poid+"?");
        builder1.setCancelable(true);
        builder1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	Toast.makeText(getApplicationContext(),
						"Yes delete ", Toast.LENGTH_LONG)
						.show();
                dialog.cancel();
                // MAKE A CALL TO THE POI SERVICE TO DELETE THIS POI
                // POISERVICE.DELETE(poiid)
                Long idPoiToDelete = poid;
                _poicntrl.deletePoi(idPoiToDelete);
                googleMap.clear();
                populateTheMapWithPoi(googleMap);
            }
        });
        builder1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	Toast.makeText(getApplicationContext(),
						"No delete ", Toast.LENGTH_LONG)
						.show();
                dialog.cancel();
                
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();
		
	}
	
	/**
	 *    Clear the markers from the map. this has a local effect 
	 *     To delete a spacific marker (poi)  for all the community 
	 *     right click on the marker ...  you will be guided don't worry ^^
	 */
	public void clearAllMarkersREQ(){
		AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(" Do you want to delete all POI s ?");
        builder1.setCancelable(true);
        builder1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	Toast.makeText(getApplicationContext(),
						"Yes delete ", Toast.LENGTH_LONG)
						.show();
                dialog.cancel();
                googleMap.clear();
            }
        });
        builder1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	Toast.makeText(getApplicationContext(),
						"No delete ", Toast.LENGTH_LONG)
						.show();
                dialog.cancel();
                
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();
		//
	}
	
	/**
	 * Request to the server to download all pois 
	 */
	public void loaddAllPOIREQ(){
		AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(" Do you want to downlaod  all POI s ?");
        builder1.setCancelable(true);
        builder1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	Toast.makeText(getApplicationContext(),
						"Yes download ", Toast.LENGTH_LONG)
						.show();
                dialog.cancel();
                googleMap.clear();
                populateTheMapWithPoi(googleMap);
            }
        });
        builder1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	Toast.makeText(getApplicationContext(),
						"No delete ", Toast.LENGTH_LONG)
						.show();
                dialog.cancel();
                
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();
		
		
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
    @Override
	protected void onSaveInstanceState(Bundle mysaver) {
		  super.onSaveInstanceState(mysaver);
		  //  save muy location
		/*  mysaver.putLong("MyLocationPoi",_myLocationPoi);
		  
		 */
		  
    }
	/**
	 *  Reload all  the POI (s) from the server  at a given interval time 
	 * @author me
	 *
	 */
    class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					
					Toast.makeText(getApplicationContext(), "Realoding of the POI (s)  ... ",
									Toast.LENGTH_SHORT).show();
					populateTheMapWithPoi(googleMap);
				};});
			
		}
  	 
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

