package org.istic.mmm_likewaze.api.task;

import java.util.HashMap;

import org.istic.mmm_likewaze.model.TypePoi;

/**
 *   Utility class for conversion of string to Poi Type 
 *  
 * @author me
 * 
 */
public class String2Poi {

	   
	    //  Map of conversion data
	    private static final HashMap<String, TypePoi> myPoimap;
	    static
	    {
	    	myPoimap = new HashMap<String, TypePoi>();
	    	myPoimap.put("ACCIDENT", TypePoi.ACCIDENT);
	    	myPoimap.put("POLICE",TypePoi.ACCIDENT);
	    	myPoimap.put("POLICE",TypePoi.POLICE);
	    	myPoimap.put("FLOOD",TypePoi.FLOOD);
	    	myPoimap.put("FIRE",TypePoi.FIRE);
	    	myPoimap.put("TRAFFICJAM",TypePoi.TRAFFICJAM);
	    	myPoimap.put("USER", TypePoi.USER);
	    	myPoimap.put("RADAR",TypePoi.RADAR);
	    	myPoimap.put("BOUCHON_SIGNALE",TypePoi.BOUCHON_SIGNALE);
	    	myPoimap.put("BOUCHON_CALCULE",TypePoi.BOUCHON_CALCULE);
	    	myPoimap.put("PIETON_S",TypePoi.PIETON_S);
	    	myPoimap.put("PIETON_E",TypePoi.PIETON_E);
	    	myPoimap.put("PIETON_W",TypePoi.PIETON_W);
	    	myPoimap.put("PIETON_NE",TypePoi.PIETON_NE);
	    	myPoimap.put("PIETON_NW",TypePoi.PIETON_NW);
	    	myPoimap.put("PIETON_SE",TypePoi.PIETON_SE);
	    	myPoimap.put("PIETON_SW",TypePoi.PIETON_SW);
	    	myPoimap.put("PIETON_N",TypePoi.PIETON_N);
	    	myPoimap.put("DANGER",TypePoi.DANGER);
	    	myPoimap.put("TRAVAUX",TypePoi.TRAVAUX);
	    }
	    /**
	     * 
	     * @param value  :  the string to covert to Poi data
	     * @return  the Poi data equivalent to the value paramter string 
	     */
	    public static TypePoi getPoiTypeEquivalent(String value){
	    	   return myPoimap.get(value);
			
		}
}
