package eu.arrowhead.client.skeleton.common;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import teamethernet.senmlapi.Label;
import teamethernet.senmlapi.SenMLAPI;

//DTO that is sent between Provider adn the datamanager. This encodes the data in to the SenML format according to IETF.org rfc8428
//This uses Team Ethernet's SenMLAPI library for converting the data into SenML Json objects.

public class SenMLJsonDTO {

    SenMLAPI snML = SenMLAPI.initJson();


    private byte [] json;
    
    
    
    public SenMLJsonDTO(){}

    public void createSenML(ProviderJSONDTO pDTO) throws IOException {
		SenMLAPI snML = SenMLAPI.initJson();
        snML.addRecord(Label.BASE_NAME.attachValue("wm-data"), Label.BASE_TIME.attachValue((double)Instant.now().getEpochSecond()));
		snML.addRecord(Label.NAME.attachValue("Time"),Label.UNIT.attachValue("Years"), Label.VALUE.attachValue(pDTO.getTimeStamp()));
		snML.addRecord(Label.NAME.attachValue("RPM"),Label.UNIT.attachValue("RPM"), Label.VALUE.attachValue(pDTO.getSpeed()));
		final List<Double> acc = pDTO.getAccelerometer();
		for(double tmp: acc) {
                    
			snML.addRecord(Label.NAME.attachValue("acceleration"), Label.UNIT.attachValue("Gs"), Label.VALUE.attachValue(tmp));
			
    }

    json = snML.getSenML();
    // System.out.println(new String(json));
		
    }
    
    public String getSenMLString() {
        return new String(this.json);
    }
}
