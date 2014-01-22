package com.istic.mmm_likewaze.remote.controller;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.util.Log;

import com.istic.mmm_likewaze.api.PoiService;
import com.istic.mmm_likewaze.api.task.ALLPoiTask;
import com.istic.mmm_likewaze.api.task.LoginTask;
import com.istic.mmm_likewaze.api.task.ServiceHandler;
import com.istic.mmm_likewaze.model.Poi;
import com.istic.mmm_likewaze.model.User;

public class RemotePoiController   implements PoiService{

	@Override
	public void addPoi(Poi p) {
		 //  To deal with it later  
	}

	@Override
	public ArrayList<Poi> getAllPoi() {
		
		
     Log.i(" R-Poi-Controller","  trying to retrieve Poi list ..... ");
		 
		 
		 ALLPoiTask poitsk = new ALLPoiTask ();
		 poitsk.setUrlQuery("http://1-dot-likewize-m2gl.appspot.com/rest/poi/getall");
		 poitsk.setOperation(ServiceHandler.GET);
		 poitsk.execute(); 
		 try {
			 poitsk.get();   //  wait till obtaining the response from the server 
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}  
		 
		 ArrayList<Poi> lipoi=  poitsk.getResultPoiList();
		 if(lipoi != null)
		       Log.i("R-Poi-Controller result : list size "," Obj pseudo :"+lipoi.size());
		 else  Log.i("R-Poi-Controller result : "," Obj NULLL !!! ");
		
		  return lipoi;
	}

}
