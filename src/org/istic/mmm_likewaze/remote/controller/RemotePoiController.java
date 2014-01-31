package org.istic.mmm_likewaze.remote.controller;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.istic.mmm_likewaze.api.PoiService;
import org.istic.mmm_likewaze.api.task.ALLPoiTask;
import org.istic.mmm_likewaze.api.task.ServiceHandler;
import org.istic.mmm_likewaze.api.task.SignalPoiTask;
import org.istic.mmm_likewaze.model.Poi;

import android.util.Log;

public class RemotePoiController implements PoiService {

	@Override
	public void addPoi(Poi p) {

		Log.i(" R-Poi-Controller", "  trying to post a poi..... ");

		SignalPoiTask spoitsk = new SignalPoiTask();
		spoitsk.setUrlQuery(ServerURL.server_url + "/rest/poi/create");
		spoitsk.set_poiToSend(p);
		spoitsk.execute();
		try {
			spoitsk.get(); // wait till obtaining the response from the server
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		Log.i(" R-Poi-Controller", "  END of  singUp the user ..... ");
	}

	@Override
	public ArrayList<Poi> getAllPoi() {

		Log.i(" R-Poi-Controller", "  trying to retrieve Poi list ..... ");

		ALLPoiTask poitsk = new ALLPoiTask();
		poitsk.setUrlQuery(ServerURL.server_url + "/rest/poi/List");
		poitsk.setOperation(ServiceHandler.GET);
		poitsk.execute();
		try {
			poitsk.get(); // wait till obtaining the response from the server
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		ArrayList<Poi> lipoi = poitsk.getResultPoiList();
		if (lipoi != null)
			Log.i("R-Poi-Controller result : list size ", " Obj pseudo :"
					+ lipoi.size());
		else
			Log.i("R-Poi-Controller result : ", " Obj NULLL !!! ");

		return lipoi;
	}

}
