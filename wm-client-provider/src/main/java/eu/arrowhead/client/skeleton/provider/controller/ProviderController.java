package eu.arrowhead.client.skeleton.provider.controller;

import java.io.IOException;
import java.net.URISyntaxException;

// import com.fasterxml.jackson.databind.SerializationFeature;
// import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
// import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eu.arrowhead.common.CommonConstants;

import eu.arrowhead.client.skeleton.common.*;

import eu.arrowhead.client.skeleton.common.ProviderCommonConstants;

import eu.arrowhead.common.exception.DataNotFoundException;

@RestController
public class ProviderController {
	
	//=================================================================================================
	// members

	//TODO: add your variables here

	@Autowired
	private WMDataService dataService;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@GetMapping(path = CommonConstants.ECHO_URI)
	public String echoService() {
		return "Got it!";
	}

	// @GetMapping(path = ProviderCommonConstants.WM_DATA_SERVICE_URI)
	// public ProviderDTO getNextProviderDTO () throws IOException, URISyntaxException {
	// 	ProviderDTO pDTO = new ProviderDTO();
	// 	pDTO = dataService.getProviderDTO();

	// 	// final XmlMapper xmlMapper = new XmlMapper();
	// 	// xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	// 	// System.out.println("\n" + xmlMapper.writeValueAsString(pDTO) + "\n");
		
	// 	if(pDTO != null) {
	// 		return pDTO;
	// 	} else {
	// 		throw new DataNotFoundException("No data left");
	// 	}

	// }

	@GetMapping(path = ProviderCommonConstants.WM_DATA_SERVICE_URI_JSON)
	ProviderJSONDTO getNextProviderJSONDTO () throws IOException, URISyntaxException {
		System.out.println("PJSONDTO");
		ProviderJSONDTO pDTO = new ProviderJSONDTO();
		pDTO = dataService.getProviderJSONDTO();
		System.out.println(pDTO);
		if(pDTO != null) {
			return pDTO;
		} else {
			throw new DataNotFoundException("No data left");
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	//TODO: implement here your provider related REST end points
}
