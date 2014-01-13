package org.istic.mmm_likewaze;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;



public class IHmap extends  android.support.v4.app.FragmentActivity 
implements OnMapClickListener {

	private GoogleMap mMap;
	LatLng myPosition;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ihmap);
		
		// Try to obtain the map from the SupportMapFragment.
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();
        
        // To Delete 
        mMap.setTrafficEnabled(true);
        mMap.setMapType(mMap.MAP_TYPE_HYBRID);// MAP_TYPE_SATELLITE
        
        
        
        //mMap.setOnMapClickListener(this);
        
        // Enabling MyLocation Layer of Google Map
        mMap.setMyLocationEnabled(true);

        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);

        if(location!=null){
		        // Getting latitude of the current location
		        double latitude = location.getLatitude();
		
		        // Getting longitude of the current location
		        double longitude = location.getLongitude();
		
		        // Creating a LatLng object for the current location
		        LatLng latLng = new LatLng(latitude, longitude);
		
		         myPosition = new LatLng(latitude, longitude);
		
		         mMap.addMarker(new MarkerOptions().position(myPosition).title("Start"));
		         
		         Log.i(" LLLLOOOC", "Lattitude:" +latitude);
		         Log.i(" LLLLOOOC", "Longitude:" +longitude);
         }
        
       
        
      //  To center  and zoom in
        
        CameraUpdate center=
                CameraUpdateFactory.newLatLng(myPosition);
            CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

            mMap.moveCamera(center);
            mMap.animateCamera(zoom);
            
           
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ihmap, menu);
		return true;
	}

	@Override
	public void onMapClick(LatLng position) {
		
		mMap.addMarker(new MarkerOptions()
							.position(position).
							icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
		
	}

}
