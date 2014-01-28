package org.istic.mmm_likewaze;

import com.google.android.gms.internal.bn;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
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
						
						setPoiBtnAction((ImageButton) childView, PoiType.RADAR);
						break;

					case R.id.BtnPanne:
						setPoiBtnAction((ImageButton) childView, PoiType.RADAR);
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

	

	private void initilizeMap() {
		
		//latitude = 47.750203;
		//longitude = -1.681212;
		
//        if (googleMap == null) {
//                googleMap = ((MapFragment) getFragmentManager().findFragmentById(
//                                R.id.map)).getMap();
//                googleMap.setMyLocationEnabled(true);
//                
//                MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Hello Maps ");
//                 
//                googleMap.addMarker(marker);
//                
//                if (googleMap == null) {
//                        Toast.makeText(getApplicationContext(),
//                                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
//                                        .show();
//                }
//        }
}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
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
