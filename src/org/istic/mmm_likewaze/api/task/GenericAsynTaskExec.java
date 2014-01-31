package org.istic.mmm_likewaze.api.task;


import android.os.AsyncTask;

public abstract class GenericAsynTaskExec extends AsyncTask<Void, Void, Void> {
	 
	
	protected int  _serviceCode=-1;
	protected String _url_Query="";
	
	//  To Force the user to enter the URL

	public  GenericAsynTaskExec (){
		  super();
	}
	
	public GenericAsynTaskExec(String Url){
		
		     super();
		    _url_Query=Url;	
	}
	
	public void setOperation(int opCode){	
		_serviceCode = opCode;
	}
	
	public void setUrlQuery(String Url){
		 _url_Query=Url;
	}
   
}