package org.istic.mmm_likewaze.model;

/**
 * 
 *   A PoiType , represents different types of Point of Interest  
 *              supported  by this application   
 * @author me
 *
 */
public enum TypePoi {
	
    USER,ACCIDENT,POLICE,FLOOD,FIRE,TRAFFICJAM,NULLTYPE,RADAR,BOUCHON_SIGNALE, 
    BOUCHON_CALCULE,DANGER,TRAVAUX,
    PIETON_N,PIETON_S,PIETON_E,PIETON_W,   //   direction pieton , nord , sude , est , west
    PIETON_NE,PIETON_NW,PIETON_SE,PIETON_SW  //  nord east , nord west , nord sud east , sud west
    ;
    
}
