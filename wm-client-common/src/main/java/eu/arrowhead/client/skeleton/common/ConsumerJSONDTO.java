package eu.arrowhead.client.skeleton.common;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "WMData")

public class ConsumerJSONDTO implements Serializable {
    private static final long serialVersionUID = 4L;

    @JsonProperty
    private double timeStamp;

    @JsonProperty
    private double speed;

   
    @JsonProperty
    private List<Double> accelerometer;
    
    public ConsumerJSONDTO(){}

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
