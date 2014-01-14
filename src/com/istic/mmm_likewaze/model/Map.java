package  com.istic.mmm_likewaze.model;
import java.util.Collection;

public class Map{

	private int idmap;
    private double rayonaction;	
	private Collection<Device> devList;
	private Collection<Poi> poiList;
    
    
	public int getIdmap() {
		return idmap;
	}
	public void setIdmap(int idmap) {
		this.idmap = idmap;
	}
	public double getRayonaction() {
		return rayonaction;
	}
	public void setRayonaction(double rayonaction) {
		this.rayonaction = rayonaction;
	}
	public Collection<Device> getDevList() {
		return devList;
	}
	public void setDevList(Collection<Device> devList) {
		this.devList = devList;
	}
	public Collection<Poi> getPoiList() {
		return poiList;
	}
	public void setPoiList(Collection<Poi> poiList) {
		this.poiList = poiList;
	}
   
    	
}