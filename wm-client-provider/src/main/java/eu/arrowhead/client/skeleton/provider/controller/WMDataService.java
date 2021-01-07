package eu.arrowhead.client.skeleton.provider.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.arrowhead.client.skeleton.common.ProviderDataReader;
import eu.arrowhead.client.skeleton.common.ProviderDTO;
import eu.arrowhead.client.skeleton.common.WMDataObject;

@Component
public class WMDataService {

    @Autowired
    private ProviderDataReader dataReader;

    public ProviderDTO getProviderDTO () throws IOException, URISyntaxException {
        final ProviderDTO wmProviderDTO = new ProviderDTO();
        WMDataObject wmData = new WMDataObject();
        wmData = dataReader.readData();
        wmProviderDTO.setTimeStamp(wmData.getWMTimeStamp());
        wmProviderDTO.setSpeed(wmData.getWMSpeed());
        wmProviderDTO.setAccelerometer(wmData.getWMAccelerometer());




        return wmProviderDTO;
    }
}
