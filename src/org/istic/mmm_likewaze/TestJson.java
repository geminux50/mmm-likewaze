package org.istic.mmm_likewaze;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.istic.likewaze.local.controller.UserController;
import com.istic.mmm_likewaze.model.User;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class TestJson extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_json);
		Log.i("OPERATION:","Calling the user controller" );
		UserController usrCntrl = new UserController(this.getResources().openRawResource(R.raw.local_user_store));
	    usrCntrl.login("user1", "pass");
	   
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test_json, menu);
		return true;
	}

}
