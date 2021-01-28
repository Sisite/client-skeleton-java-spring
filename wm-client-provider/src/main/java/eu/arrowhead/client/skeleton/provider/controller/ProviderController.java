package eu.arrowhead.client.skeleton.provider.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;

import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.common.dto.shared.ServiceQueryFormDTO;
import eu.arrowhead.client.skeleton.common.*;

import eu.arrowhead.client.skeleton.common.ProviderCommonConstants;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.DataNotFoundException;
import eu.arrowhead.common.exception.InvalidParameterException;

import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.core.CoreSystem;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.dto.shared.OrchestrationFlags.Flag;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO.Builder;
import eu.arrowhead.common.dto.shared.OrchestrationResponseDTO;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.common.dto.shared.ServiceQueryFormDTO;

@RestController
public class ProviderController {
	
	//=================================================================================================
	// members

	//TODO: add your variables here

	@Autowired
	private WMDataService dataService;

	@Autowired
	private ArrowheadService arrowheadService;
	
	

	private final Logger logger = LogManager.getLogger( ProviderController.class );


	@GetMapping(path = ProviderCommonConstants.WM_DATA_SERVICE_URI_JSON)
	ProviderJSONDTO getNextProviderJSONDTO (@RequestParam MultiValueMap<String, String> params) throws IOException, URISyntaxException {
		
		Iterator<String> it = params.keySet().iterator();
		
		while (it.hasNext()) {
			String par = it.next();
			if(par.equals(ProviderCommonConstants.REQUEST_PARAM_KEY_NEXT)) {
				if(!params.getFirst(par).equals(ProviderCommonConstants.REQUEST_PARAM_NEXT)) {
					throw new InvalidParameterException("Invalid parameter", HttpStatus.SC_BAD_REQUEST, ProviderCommonConstants.WM_DATA_SERVICE_URI_JSON);
				}
			}
		}
		
		ProviderJSONDTO pDTO = new ProviderJSONDTO();
		pDTO = dataService.getProviderJSONDTO();



		if(pDTO != null) {
			putHistorianData(pDTO);
			return pDTO;
		} else {
			throw new DataNotFoundException("No data left", HttpStatus.SC_INTERNAL_SERVER_ERROR, ProviderCommonConstants.WM_DATA_SERVICE_URI);
		}
	}

	//Convert to SenML and start the orchestration for the historian service
	private void putHistorianData(ProviderJSONDTO pDTO) throws IOException {
		arrowheadService.updateCoreServiceURIs(CoreSystem.ORCHESTRATOR);
		final SenMLJsonDTO senMLJsonDTO = new SenMLJsonDTO();
		senMLJsonDTO.createSenML(pDTO);
		String message = senMLJsonDTO.getSenMLString();
		OrchestrationResultDTO orchRes = orchestrate("historian");
		historianRequest(orchRes, message);
	}

	// Consume the historian service and store the data
	private void historianRequest(OrchestrationResultDTO orchRes, Object snMLMessage) {
		final String interfaceName = orchRes.getInterfaces().get(0).getInterfaceName();
		final String secToken = orchRes.getAuthorizationTokens() == null ? null : orchRes.getAuthorizationTokens().get(interfaceName);
		final HttpMethod httpMethod = HttpMethod.PUT;
		final String address = orchRes.getProvider().getAddress();
		final int port = orchRes.getProvider().getPort();
		final String serviceUri = orchRes.getServiceUri() + ProviderCommonConstants.HIST_PROV_URI;

		String response = arrowheadService.consumeServiceHTTP(String.class, httpMethod, address, port, serviceUri, interfaceName, secToken, snMLMessage);
		System.out.println("\n Response : \n" + response + "\n");

	}

	//Orchestration request for historian service
	private OrchestrationResultDTO orchestrate (final String serviceName) {
		final ServiceQueryFormDTO srvQDTO = new ServiceQueryFormDTO();
		srvQDTO.setServiceDefinitionRequirement("historian");
		final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
		final OrchestrationFormRequestDTO  orchFormReq = orchestrationFormBuilder.requestedService(srvQDTO).flag(Flag.MATCHMAKING, true).flag(Flag.OVERRIDE_STORE, true).build();
		final OrchestrationResponseDTO orchResp = arrowheadService.proceedOrchestration(orchFormReq);
		if (orchResp == null) {
			logger.info("No orchestration response received");
			} else if (orchResp.getResponse().isEmpty()) {
				logger.info("No provider found dm");
			} else {
				final OrchestrationResultDTO orchRes = orchResp.getResponse().get(0);
				return orchRes;
			}
			throw new ArrowheadException("Unsuccesful orchestration: " + serviceName);
	}
	
	//-------------------------------------------------------------------------------------------------
	//TODO: implement here your provider related REST end points


}
