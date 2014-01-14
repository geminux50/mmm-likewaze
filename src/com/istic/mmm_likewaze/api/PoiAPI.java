package com.istic.mmm_likewaze.api;

import java.util.ArrayList;

import com.istic.mmm_likewaze.model.Poi;

public interface PoiAPI {

	public void addPoi(Poi p);
	public ArrayList<Poi> getAllPoi(); 
}
