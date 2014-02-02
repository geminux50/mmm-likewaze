package org.istic.mmm_likewaze.api;

import java.util.ArrayList;

import org.istic.mmm_likewaze.model.Poi;

public interface PoiService {

	/**
	 * 
	 * @param p  : Poi to be added to the server 
	 * @return   : The  instance of the Poi in the data base  
	 */
	public Poi addPoi(Poi p);
	
	/**
	 * 
	 * @return  The List of all existing Poi 
	 */
	public ArrayList<Poi> getAllPoi(); 
	
	/**
	 *   Update a given Poi by setting its new location
	 *   
	 * @param poiId : the identifier of poi
	 * @param latit : latitude of Poi
	 * @param longit : longitude of Poi
	 * @return : The updated Poi
	 */
	public Poi updatePoi(Long poiId, double latit, double longit);
	
	/**
	 *   Delete a Poi 
	 * @param poiId  :  theidentifier of Poi to be deleted  
	 * @return  : The Poi deleted
	 */
	public Poi deletePoi(Long poiId);
}
