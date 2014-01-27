package com.istic.mmm_likewaze.api.task;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.istic.mmm_likewaze.model.Poi;
import com.istic.mmm_likewaze.model.TypePoi;
import com.istic.mmm_likewaze.model.User;
public class ALLPoiTask  extends GenericAsynTaskExec{

	
	private ArrayList<Poi> _poiList; 
	
	@Override
	protected Void doInBackground(Void... arg0) {
		// Query the server to get PoiList 
		
		 // Creating service handler class instance
        ServiceHandler sh = new ServiceHandler();

        String jsonStr = sh.makeServiceCallGET(_url_Query, null);
        Log.d("Response: ", "> " + jsonStr);

        JSONTokener tokener = new JSONTokener(jsonStr);
        try {
        	JSONArray jsonArray = new JSONArray(tokener);
        	_poiList = new ArrayList<Poi>();
		        for (int index = 0; index < jsonArray.length(); index++) {
		            //Set both values into the listview
		            JSONObject jsonObject = jsonArray.getJSONObject(index);
		            if(jsonObject !=null){
		            	///Log.i("Obj User: "," :"+jsonObject.get("pseudo")+" -- v:"+jsonObject.get("passwd") );
		            	Poi currPOI = new Poi();
		            	//currPOI.setIdGpsPts(Integer.parseInt(jsonObject.get("idGpsPts").toString()));
		            	currPOI.setCurLat(Double.parseDouble(jsonObject.get("curLat").toString()));
		            	currPOI.setCurLong(Double.parseDouble(jsonObject.get("curLong").toString()));
		            	currPOI.setLabel(jsonObject.get("label").toString());
		            	currPOI.setType(getPoiTypeEquivalent(jsonObject.get("type").toString()));
		            	_poiList.add(currPOI);
		            }
		           
		        }
			        
		           // and others 
		       
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	//  Returns the Poi list retrieved from the server ... 
	public ArrayList<Poi> getResultPoiList(){
		
		return _poiList;
	}
	
	
	private TypePoi getPoiTypeEquivalent(String value){
		
		//ACCIDENT,POLICE,FLOOD,FIRE,TRAFFICJAM; 
		if(value.equals("ACCIDENT"))  return TypePoi.ACCIDENT;
		if(value.equals("POLICE"))  return TypePoi.POLICE;
		if(value.equals("FLOOD"))  return TypePoi.FLOOD;
		if(value.equals("FIRE"))  return TypePoi.FIRE;
		if(value.equals("TRAFFICJAM"))  return TypePoi.TRAFFICJAM;
		if(value.equals("USER"))  return TypePoi.USER;
		return TypePoi.NULLTYPE;
	}
}
