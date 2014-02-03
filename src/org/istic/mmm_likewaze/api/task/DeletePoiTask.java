package org.istic.mmm_likewaze.api.task;

import org.istic.mmm_likewaze.model.Poi;
import org.istic.mmm_likewaze.model.TypePoi;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

/**
 * 
 *   An Asynch Task to delete a POi
 * 
 * @author me
 *
 */
public class DeletePoiTask  extends GenericAsynTaskExec {
	
	
   private Poi _deletedPoi;
	
	
	public Poi get_deletedPoi() {
		return _deletedPoi;
	}


	public void set_deletedPoi(Poi _updatedPoi) {
		this._deletedPoi = _updatedPoi;
	}


	@Override
	protected Void doInBackground(Void... arg0) {
		 // Creating service handler class instance
        ServiceHandler sh = new ServiceHandler();

        Log.i("info : ","*****  URL FOR DELETION IS ::  "+_url_Query);   
        
        String jsonStr = sh.makeServiceCallGET(_url_Query, null);
        Log.d("Response: ", "> " + jsonStr);

        if( jsonStr == null ){ _deletedPoi=null;
    	return  null; }
        
        JSONTokener tokener = new JSONTokener(jsonStr);
        
        try {
	        JSONObject jsonObject= new JSONObject(tokener);
	        if(jsonObject.get("curLat").equals(0)){ 
	        	_deletedPoi=null;
	        	return  null;
	         } 
	      
	         Log.i("Obj Poi: "," : lat "+jsonObject.get("curLat")+" -- v:"+jsonObject.get("curLong") );
	         _deletedPoi = new Poi();
	         _deletedPoi.setCurLat(Double.parseDouble(jsonObject.get("curLat").toString()));
	         _deletedPoi.setCurLong(Double.parseDouble(jsonObject.get("curLong").toString()));
	         _deletedPoi.setIdpoi(Long.parseLong(jsonObject.get("idpoi").toString()));
	         _deletedPoi.setLabel(jsonObject.get("label").toString());
	         _deletedPoi.setType(getPoiTypeEquivalent(jsonObject.get("type").toString()));
	        
			       
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
