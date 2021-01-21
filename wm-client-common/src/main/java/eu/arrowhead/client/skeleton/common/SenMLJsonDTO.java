package eu.arrowhead.client.skeleton.common;

import java.io.IOException;
import java.util.List;

import teamethernet.senmlapi.Label;
import teamethernet.senmlapi.SenMLAPI;

public class SenMLJsonDTO {

    SenMLAPI snML = SenMLAPI.initJson();


    private byte [] json;
    
    
    
    public void SenMLJSONDTO(){}

    public void createSenML(ProviderJSONDTO pDTO) throws IOException {
		SenMLAPI snML = SenMLAPI.initJson();
		snML.addRecord(Label.BASE_NAME.attachValue("wm1"), Label.BASE_TIME.attachValue(pDTO.getTimeStamp()), Label.UNIT.attachValue("RPM"), Label.VALUE.attachValue(pDTO.getSpeed()));
		final List<Double> acc = pDTO.getAccelerometer();
		for(int i = 0; i < 16383; i++) {
                    
			snML.addRecord(Label.NAME.attachValue("acceleration"), Label.UNIT.attachValue("Gs"), Label.VALUE.attachValue(acc.get(i)));
			
		}
		json = snML.getSenML();
		
    }
    
    public byte [] getSenMLByte() {
        return this.json;
    }
}
