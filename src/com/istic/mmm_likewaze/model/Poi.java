package  com.istic.mmm_likewaze.model;
import java.util.Collection;


public class Poi extends GpsPoint{
	
	private TypePoi type;
	//@Lob
	//private array[Byte]
	private String label;
	private int time2live;
	private Device device;
	private Map  map;
	
	
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