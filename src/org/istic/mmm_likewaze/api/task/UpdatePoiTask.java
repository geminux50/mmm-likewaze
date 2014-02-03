package org.istic.mmm_likewaze.api.task;

import org.istic.mmm_likewaze.model.Poi;
import org.istic.mmm_likewaze.model.TypePoi;
import org.istic.mmm_likewaze.model.User;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;


/**
 * 
 *    ASynch Task for updating a Poi
 * 
 * @author me 
 *  
 *
 */
public class UpdatePoiTask  extends GenericAsynTaskExec {

	private Poi _updatedPoi;
	
	
	public Poi get_updatedPoi() {
		return _updatedPoi;
	}


	public void set_updatedPoi(Poi _updatedPoi) {
		this._updatedPoi = _updatedPoi;
	}


	@Override
	protected Void doInBackground(Void... arg0) {
		 // Creating service handler class instance
        ServiceHandler sh = new ServiceHandler();

        String jsonStr = sh.makeServiceCallGET(_url_Query, null);
        Log.d("Response: ", "> " + jsonStr);
        if( jsonStr == null ){ _updatedPoi=null;
    	return  null; }
        
        
        JSONTokener tokener = new JSONTokener(jsonStr);
        
        try {
	        JSONObject jsonObject= new JSONObject(tokener);
	        if(jsonObject.get("curLat").equals(0)){ 
	        	_updatedPoi=null;
	        	return  null;
	         } 
	      
	         Log.i("Obj Poi: "," : lat "+jsonObject.get("curLat")+" -- v:"+jsonObject.get("curLong") );
	         _updatedPoi = new Poi();
	         _updatedPoi.setCurLat(Double.parseDouble(jsonObject.get("curLat").toString()));
	         _updatedPoi.setCurLong(Double.parseDouble(jsonObject.get("curLong").toString()));
	         _updatedPoi.setIdpoi(Long.parseLong(jsonObject.get("idpoi").toString()));
	         _updatedPoi.setLabel(jsonObject.get("label").toString());
	         _updatedPoi.setType(getPoiTypeEquivalent(jsonObject.get("type").toString()));
	        
			       
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			return null;
		
	}

    private TypePoi getPoiTypeEquivalent(String value){
    	
    	TypePoi resp = String2Poi.getPoiTypeEquivalent(value); 
		if ( resp== null )   return TypePoi.NULLTYPE;
		else return resp;
	}
}
