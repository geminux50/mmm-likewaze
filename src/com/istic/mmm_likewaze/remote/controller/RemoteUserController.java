package com.istic.mmm_likewaze.remote.controller;


import java.util.concurrent.ExecutionException;
import android.util.Log;
import com.istic.mmm_likewaze.api.UserService;
import com.istic.mmm_likewaze.api.task.LoginTask;
import com.istic.mmm_likewaze.api.task.ServiceHandler;
import com.istic.mmm_likewaze.api.task.SignUpTask;
import com.istic.mmm_likewaze.model.User;

public class RemoteUserController    implements UserService   {

	
	//
	public RemoteUserController (){
		
	}
	
	
	@Override
	public User login(String pseudo, String passwd){
		 Log.i(" R-User-Controller","  trying to logg the user ..... ");
		 
		 
		 LoginTask logtsk = new LoginTask();
		 logtsk.setUrlQuery("http://1-dot-likewize-m2gl.appspot.com/rest/user/login/"+pseudo+"/"+passwd);
		 logtsk.setOperation(ServiceHandler.GET);
		 logtsk.execute(); 
		 try {
				logtsk.get();   //  wait till obtaining the response from the server 
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}  
		 
		 User us=  logtsk.getResultLogin();
		 if(us != null)
		       Log.i("R-User-Controller result : "," Obj pseudo :"+us.getPseudo()+"  Obj passwd :"+us.getPasswd());
		 else  Log.i("R-User-Controller result : "," Obj NULLL !!! ");
		
		 return us;
	}

	@Override
	public void signUp(User  us) {
		
      Log.i(" R-User-Controller","  trying to singUp the user ..... ");
		 
		 
		 SignUpTask suptsk = new  SignUpTask ();
		 suptsk.setUrlQuery("http://1-dot-likewize-m2gl.appspot.com/rest/user/adduser");
		 suptsk.setUser(us);
		 suptsk.execute(); 
		 try {
			 suptsk.get();   //  wait till obtaining the response from the server 
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}  
		 
		 Log.i(" R-User-Controller","  END of  singUp the user ..... ");
		
	}

}
