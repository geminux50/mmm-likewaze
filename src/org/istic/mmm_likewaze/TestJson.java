package org.istic.mmm_likewaze;


import java.util.ArrayList;

import com.istic.mmm_likewaze.model.Poi;
import com.istic.mmm_likewaze.model.TypePoi;
import com.istic.mmm_likewaze.model.User;
import com.istic.mmm_likewaze.remote.controller.RemotePoiController;
import com.istic.mmm_likewaze.remote.controller.RemoteUserController;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class TestJson extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_json);
		
		Log.i("OPERATION "," call  Signal  a Poi");
		
		RemotePoiController  poicntrl = new RemotePoiController();
		Poi po= new Poi();
		po.setCurLat(48.08441);
		po.setCurLong(-1.691246);
		po.setLabel("Brequigny Jam traffic");
		po.setTime2live(5);
		po.setType(TypePoi.POLICE);
	     	
	    poicntrl.addPoi(po);
	    
		Log.i("OPERATION "," end of  call  Signal  a Poi");
		
		/*Log.i("OPERATION:","Calling the user controller  remote  to sign up  " );
		RemoteUserController usrcntrl = new RemoteUserController();
	    User u = new User();
	    u.setEmail("user4@yahoo.fr");
	    u.setPasswd("pass4");
	    u.setPseudo("user4");
	    
	    usrcntrl.signUp(u);
	    
		Log.i("OPERATION:"," END  Of Calling the user controller  remote  to sign up " );*/
		
		
		// Login Scenario ..... 
		
		/*Log.i("OPERATION:","Calling the user controller  remote  to Log in  " );
		
		RemoteUserController usrcntrl = new RemoteUserController();
		User u =usrcntrl.login("user1", "pass1");
		if(u==null){
			Log.i("Log Result :" ," Loggin failed ! ");
		}else{
			Log.i("Log Result :" ," Loggin success  ! ");
		}
		*/
		// Retrieving PoiList scenario .....
		
		/*Log.i("OPERATION:","Calling the poi controller to Retrievce Poi Points  ");
		
		RemotePoiController  poicntrl = new RemotePoiController();
		ArrayList<Poi>  poiList= poicntrl.getAllPoi();
		if(poiList==null){
			Log.i("Poi Result :" ," Poi retrieved a List failed ! ");
		}else{
			Log.i("Poi Result :" ," Poi retrieved a List  sucess  ! ");
		}*/
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test_json, menu);
		return true;
	}

}
