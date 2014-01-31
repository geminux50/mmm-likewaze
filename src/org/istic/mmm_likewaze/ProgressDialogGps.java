package org.istic.mmm_likewaze;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class ProgressDialogGps extends ProgressDialog {

	List<LatLng> history;
	int nbOfGeoPts;

	public ProgressDialogGps(int nbOfGeoPts, Context context) {
		super(context);
		this.nbOfGeoPts = nbOfGeoPts;
		this.history = new ArrayList<LatLng>();
		this.setMax(nbOfGeoPts);
		this.setProgress(0);
	}

	public void updateLocation(Location location) {
		if (history.size() < nbOfGeoPts) {
			if (location != null) {
				history.add(new LatLng(location.getLatitude(), location
						.getLongitude()));
				this.incrementProgressBy(1);
				Log.i(this.getClass().getName(),"adding a new position to history");
			}
		}
	}
}
