package org.istic.mmm_likewaze.api.task;

import org.istic.mmm_likewaze.model.User;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;


/**
 * 
 *   ---  This class is for signing up (registration )the user inside the system
 *   POST
 * */
public class SignUpTask   extends GenericAsynTaskExec{

	private User _userToSignUp =null;
	private User _userSignedUp = null;
	
	
	public void setUser(User usr){
	  _userToSignUp = usr;	
	}
	
	public User getSignedUpUser(){
		return _userSignedUp;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		
		 ServiceHandler sh = new ServiceHandler();
		 String jsonStr= sh.makeServiceCallPost(_url_Query,_userToSignUp);
		 Log.d("Response  of post : ", "> " + jsonStr);
		 JSONTokener tokener = new JSONTokener(jsonStr);
	        try {
				        JSONObject jsonObject= new JSONObject(tokener);
				        if(jsonObject.get("passwd").equals(null)){ 
				        	_userSignedUp=null;
				        	return  null;
				         } 
				      
				        Log.i("Obj User: "," :"+jsonObject.get("pseudo")+" -- v:"+jsonObject.get("passwd") );
				        _userSignedUp = new User();
				        _userSignedUp.setPasswd(jsonObject.get("passwd").toString());
				        _userSignedUp.setPseudo(jsonObject.get("pseudo").toString());
				        _userSignedUp.setEmail(jsonObject.get("email").toString());
				        _userSignedUp.setUserId(Long.parseLong(jsonObject.get("userId").toString()));
			            
			           // and others 
			       
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 return null;
	}

}
