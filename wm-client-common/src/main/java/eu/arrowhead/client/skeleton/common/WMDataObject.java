package eu.arrowhead.client.skeleton.common;

import java.io.Serializable;
import java.util.List;


public class WMDataObject implements Serializable{
    private static final long serialVersionUID = 4L;

    private double wmTimeStamp;
    private double wmSpeed;
    private List<Double> wmAccelerometer;

    public WMDataObject() {}

    public WMDataObject(final double wmTimeStamp, final double wmSpeed, final List<Double> wmAccelerometer) {
        this.wmTimeStamp = wmTimeStamp;
        this.wmSpeed = wmSpeed;
        this.wmAccelerometer = wmAccelerometer;

    }

    public void setWMTimeStamp(double timeStamp) {
        this.wmTimeStamp = timeStamp;
    }

    public void setWMSpeed (double speed) {
        this.wmSpeed = speed;
    }

    public void setWMAccelerometer (List<Double> wmAccelerometer) {
        this.wmAccelerometer = wmAccelerometer;
    }
    public double getWMTimeStamp() {return this.wmTimeStamp;}
    public double getWMSpeed() {return this.wmSpeed;}
    public List<Double> getWMAccelerometer () {return this.wmAccelerometer;}

}
