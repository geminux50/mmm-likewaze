package  org.istic.mmm_likewaze.model;

import java.io.Serializable;
import java.util.Collection;

public class Device implements Serializable  {
  
	private static final long serialVersionUID = 1L;
	private int deviceId; 
	private GpsPoint currentPosition;
	private Collection<Poi> poiList;
	private Map map; 	
	private Speed myspeed;
	private User user;
    
    
	public int getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}
	public GpsPoint getCurrentPosition() {
		return currentPosition;
	}
	public void setCurrentPosition(GpsPoint currentPosition) {
		this.currentPosition = currentPosition;
	}
	public Collection<Poi> getPoiList() {
		return poiList;
	}
	public void setPoiList(Collection<Poi> poiList) {
		this.poiList = poiList;
	}
	public Map getMap() {
		return map;
	}
	public void setMap(Map map) {
		this.map = map;
	}
	public Speed getMyspeed() {
		return myspeed;
	}
	public void setMyspeed(Speed myspeed) {
		this.myspeed = myspeed;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
  
	
}