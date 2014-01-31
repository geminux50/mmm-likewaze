package org.istic.mmm_likewaze.api.task;

import org.istic.mmm_likewaze.model.User;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

public class LoginTask   extends GenericAsynTaskExec  {

	private  User _resLogin=null;
	
	@Override
	protected Void doInBackground(Void... params) {
		
		 // Creating service handler class instance
        ServiceHandler sh = new ServiceHandler();

        String jsonStr = sh.makeServiceCallGET(_url_Query, null);
        Log.d("Response: ", "> " + jsonStr);

        JSONTokener tokener = new JSONTokener(jsonStr);
        try {
			        JSONObject jsonObject= new JSONObject(tokener);
			        if(jsonObject.get("passwd").equals(null)){ 
			           _resLogin=null;
			        	return  null;
			         } 
			      
			        Log.i("Obj User: "," :"+jsonObject.get("pseudo")+" -- v:"+jsonObject.get("passwd") );
			        _resLogin = new User();
		            _resLogin.setPasswd(jsonObject.get("passwd").toString());
		            _resLogin.setPseudo(jsonObject.get("pseudo").toString());
		            _resLogin.setEmail(jsonObject.get("email").toString());
		            _resLogin.setUserId(Long.parseLong(jsonObject.get("userId").toString()));
		            
		           // and others 
		       
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	}

	
	/**  Return the Login result */
	
	public User getResultLogin(){
		 return _resLogin;
	}
	
}
