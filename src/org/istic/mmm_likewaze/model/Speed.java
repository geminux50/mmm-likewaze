package  org.istic.mmm_likewaze.model;

public class Speed {
  
	private int idSpd; 	
    private double curSpeed;
    private double speedlimite;
    private Device device;
	public int getIdSpd() {
		return idSpd;
	}
	public void setIdSpd(int idSpd) {
		this.idSpd = idSpd;
	}
	public double getCurSpeed() {
		return curSpeed;
	}
	public void setCurSpeed(double curSpeed) {
		this.curSpeed = curSpeed;
	}
	public double getSpeedlimite() {
		return speedlimite;
	}
	public void setSpeedlimite(double speedlimite) {
		this.speedlimite = speedlimite;
	}
	public Device getDevice() {
		return device;
	}
	public void setDevice(Device device) {
		this.device = device;
	}
    
    
	
}