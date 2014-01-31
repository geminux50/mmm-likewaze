package  org.istic.mmm_likewaze.model;
import java.util.Collection;


public class Poi {
	
    
	private  Long idpoi; 
	private  double curLat;
	private  double curLong;
	
	private TypePoi type;
	private String label;
	private int time2live;
	private Device device;
	private Map  map;
	
	
	
	
	public Long getIdpoi() {
		return idpoi;
	}
	public void setIdpoi(Long idpoi) {
		this.idpoi = idpoi;
	}
	public double getCurLat() {
		return curLat;
	}
	public void setCurLat(double curLat) {
		this.curLat = curLat;
	}
	public double getCurLong() {
		return curLong;
	}
	public void setCurLong(double curLong) {
		this.curLong = curLong;
	}
	public TypePoi getType() {
		return type;
	}
	public void setType(TypePoi type) {
		this.type = type;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public int getTime2live() {
		return time2live;
	}
	public void setTime2live(int time2live) {
		this.time2live = time2live;
	}
	public Device getDevice() {
		return device;
	}
	public void setDevice(Device device) {
		this.device = device;
	}
	public Map getMap() {
		return map;
	}
	public void setMap(Map map) {
		this.map = map;
	}
	
    

}