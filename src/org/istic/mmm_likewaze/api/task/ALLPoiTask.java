package org.istic.mmm_likewaze.api.task;

import java.util.ArrayList;

import org.istic.mmm_likewaze.model.Poi;
import org.istic.mmm_likewaze.model.TypePoi;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;
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
		            	currPOI.setIdpoi(Long.parseLong(jsonObject.get("idpoi").toString()));
		            	currPOI.setCurLat(Double.parseDouble(jsonObject.get("curLat").toString()));
		            	currPOI.setCurLong(Double.parseDouble(jsonObject.get("curLong").toString()));
		            	currPOI.setLabel(jsonObject.get("label").toString());
		            	currPOI.setType(getPoiTypeEquivalent(jsonObject.get("type").toString()));
		            	_poiList.add(currPOI);
		            	 Log.d(" ******  This poi id is : ", "> " + currPOI.getIdpoi());
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
		
		TypePoi resp = String2Poi.getPoiTypeEquivalent(value); 
		if ( resp== null )   return TypePoi.NULLTYPE;
		else return resp;
	}
}
