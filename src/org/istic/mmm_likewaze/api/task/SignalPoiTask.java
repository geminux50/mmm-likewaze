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
 * @author bouss
 *   
 *  An  asynchronous task to signal a Poi  
 */
public class SignalPoiTask extends GenericAsynTaskExec{

	private Poi _poiToSend =null; // The Poi  to be sent to the server 
	private Poi _poiInstance= null;//  The instance of _poiToSend in the server
	
	
	public Poi get_poiInstance() {
		return _poiInstance;
	}

	public void set_poiInstance(Poi _poiInstance) {
		this._poiInstance = _poiInstance;
	}

	public Poi get_poiToSend() {
		return _poiToSend;
	}

	public void set_poiToSend(Poi _poiToSend) {
		this._poiToSend = _poiToSend;
	}

	@Override
	protected Void doInBackground(Void... params) {
		
		 ServiceHandler sh = new ServiceHandler();
		 String resp= sh.makeServiceCallPost(_url_Query,_poiToSend );
		 Log.d("Response  of post : ", "> " + resp);
		 
		 if( resp == null ){ _poiInstance=null;
	    	return  null; }
		 
		 JSONTokener tokener = new JSONTokener(resp);
	        try {
				        JSONObject jsonObject= new JSONObject(tokener);
				        if(jsonObject.get("curLat").equals(0)){ 
				        	 _poiInstance=null;
				        	return  null;
				         } 
				      
				        Log.i("Obj Poi: "," : lat "+jsonObject.get("curLat")+" -- v:"+jsonObject.get("curLong") );
				        _poiInstance = new Poi();
				        _poiInstance.setCurLat(Double.parseDouble(jsonObject.get("curLat").toString()));
				        _poiInstance.setCurLong(Double.parseDouble(jsonObject.get("curLong").toString()));
				        _poiInstance.setIdpoi(Long.parseLong(jsonObject.get("idpoi").toString()));
				        _poiInstance.setLabel(jsonObject.get("label").toString());
				        _poiInstance.setType(getPoiTypeEquivalent(jsonObject.get("type").toString()));
				        
			           // and others 
			       
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
