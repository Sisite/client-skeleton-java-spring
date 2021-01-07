package eu.arrowhead.client.skeleton.common;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "WMData")

public class ProviderDTO implements Serializable {
    private static final long serialVersionUID = 4L;

    @JacksonXmlProperty
    private double timeStamp;

    @JacksonXmlProperty
    private double speed;

   
    @JacksonXmlProperty(localName = "value")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Double> accelerometer;
    
    public ProviderDTO(){}

    public double getTimeStamp() {return timeStamp; }
    public double getSpeed() {return speed;}
    public List<Double> getAccelerometer() {return accelerometer;}
    public void setTimeStamp(double timeStamp) {
        this.timeStamp = timeStamp;
    }
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    public void setAccelerometer(List<Double> accelerometer) {
        this.accelerometer = accelerometer;
        
    }
}
