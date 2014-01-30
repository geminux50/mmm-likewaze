package com.istic.mmm_likewaze.api.task;

import android.util.Log;

import com.istic.mmm_likewaze.model.Poi;
import com.istic.mmm_likewaze.model.User;

/**
 * 
 * @author bouss
 *   
 *  An  asynchronous task to signal a Poi  
 */
public class SignalPoiTask extends GenericAsynTaskExec{

	private Poi _poiToSend =null;
	
	
	
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
		 
		 return null;
	}
	
}
