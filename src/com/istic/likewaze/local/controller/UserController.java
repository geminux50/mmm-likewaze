package com.istic.likewaze.local.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.istic.mmm_likewaze.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.graphics.PorterDuff.Mode;
import android.util.Log;

import com.istic.mmm_likewaze.api.UserService;
import com.istic.mmm_likewaze.model.User;

public class UserController  implements UserService {

	private InputStream USER_PATH_NAME;
	
	public UserController(InputStream  filepath){
		USER_PATH_NAME=filepath;
	}
	@Override
	public User login(String _pseudo, String _passwd) {
		
		try
	    {
	        //Load File
	        BufferedReader jsonReader = new BufferedReader(new InputStreamReader(USER_PATH_NAME));
	       
	        StringBuilder jsonBuilder = new StringBuilder();
	        for (String line = null; (line = jsonReader.readLine()) != null;) {
	        jsonBuilder.append(line).append("\n");
	        }
	 
	        //Parse Json
	        JSONTokener tokener = new JSONTokener(jsonBuilder.toString());
	        JSONArray jsonArray = new JSONArray(tokener);
	 
	        ArrayList<String> fields = new ArrayList<String>();
	        for (int index = 0; index < jsonArray.length(); index++) {
	            //Set both values into the listview
	            JSONObject jsonObject = jsonArray.getJSONObject(index);
	            //fields.add(jsonObject.getString("pseudo") + " - " + jsonObject.getString("passwd"));
	            Log.i("Obj User: "," :"+jsonObject.get("pseudo")+" -- v:"+jsonObject.get("passwd") );
	            if(jsonObject.get("pseudo").equals(_pseudo)&& jsonObject.get("passwd").equals(_passwd)){
	            	Log.i(" RESULT LOGIN :","Login Accepted "); break;
	            }
	        }
	 
	       // setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fields));
	    } catch (FileNotFoundException e) {
	        Log.e("jsonFile", "file not found");
	    } catch (IOException e) {
	        Log.e("jsonFile", "ioerror");
	    } catch (JSONException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	@Override
	public User signUp(User usr) {
		// TODO Auto-generated method stub
		return null;
	}




}
