package com.istic.mmm_likewaze.api.task;

import android.util.Log;

import com.istic.mmm_likewaze.model.User;


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
		 String resp= sh.makeServiceCallPost(_url_Query,_userToSignUp);
		 Log.d("Response  of post : ", "> " + resp);
		 
		 return null;
	}

	
	
	
}
