package org.istic.mmm_likewaze.api;

import java.util.ArrayList;

import org.istic.mmm_likewaze.model.Poi;

public interface PoiService {

	public void addPoi(Poi p);
	public ArrayList<Poi> getAllPoi(); 
}
