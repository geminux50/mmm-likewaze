package org.istic.mmm_likewaze.remote.controller;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.istic.mmm_likewaze.api.PoiService;
import org.istic.mmm_likewaze.api.task.ALLPoiTask;
import org.istic.mmm_likewaze.api.task.DeletePoiTask;
import org.istic.mmm_likewaze.api.task.ServiceHandler;
import org.istic.mmm_likewaze.api.task.SignalPoiTask;
import org.istic.mmm_likewaze.api.task.UpdatePoiTask;
import org.istic.mmm_likewaze.model.Poi;

import android.util.Log;

/**
 * 
 *   Poi services 
 * @author me
 *
 */
public class RemotePoiController implements PoiService {

	@Override
	public Poi addPoi(Poi p) {

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
        Poi resPoi = spoitsk.get_poiInstance();
        if(resPoi == null)
        	Log.i(" R-Poi-Controller", "  END of resgistering a poi Faile..... ");
        else
		Log.i(" R-Poi-Controller", "  END of resgistering a poi  Ok..... ");
		return resPoi;
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

	@Override
	public Poi updatePoi(Long poiId, double latit, double longit) {
		
		Log.i(" R-Poi-Controller", "  trying to update a poi..... ");

		UpdatePoiTask spoitsk = new UpdatePoiTask();
		spoitsk.setUrlQuery(ServerURL.server_url + "/rest/poi/updatePoi/"+ poiId+"/"+latit+"/"+longit);
		spoitsk.setOperation(ServiceHandler.GET);
		spoitsk.execute();
		try {
			spoitsk.get(); // wait till obtaining the response from the server
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
        Poi resPoi = spoitsk.get_updatedPoi();
        if(resPoi == null)
        	Log.i(" R-Poi-Controller", "  END of updating a poi Faile..... ");
        else
		Log.i(" R-Poi-Controller", "  END of updating a poi  Ok..... ");
		return resPoi;
	}

	@Override
	public Poi deletePoi(Long poiId) {
		
		
		Log.i(" R-Poi-Controller", "  trying to delete a poi..... ");

		DeletePoiTask spoitsk = new DeletePoiTask();
		spoitsk.setUrlQuery(ServerURL.server_url+"/rest/poi/deletePoi/"+poiId);
		spoitsk.setOperation(ServiceHandler.GET);
		spoitsk.execute();
		try {
			spoitsk.get(); // wait till obtaining the response from the server
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
        Poi resPoi = spoitsk.get_deletedPoi();
        if(resPoi == null)
        	Log.i(" R-Poi-Controller", "  END of deleting a poi Faile..... ");
        else
		Log.i(" R-Poi-Controller", "  END of deleting a poi  Ok..... ");
		return resPoi;
	}

}
