package eu.arrowhead.client.skeleton.consumer;

import java.io.IOException;
import java.time.Instant;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;


import eu.arrowhead.client.skeleton.common.ProviderCommonConstants;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.dto.shared.OrchestrationFlags.Flag;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO.Builder;
import eu.arrowhead.common.dto.shared.OrchestrationResponseDTO;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.common.dto.shared.ServiceQueryFormDTO;
import eu.arrowhead.common.exception.ArrowheadException;
import teamethernet.senmlapi.Label;
import teamethernet.senmlapi.SenMLAPI;
import eu.arrowhead.client.skeleton.common.ProviderDTO;
import eu.arrowhead.client.skeleton.common.ProviderJSONDTO;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, ProviderCommonConstants.BASE_PACKAGE}) //TODO: add custom packages if any
public class ConsumerMain implements ApplicationRunner {
    
    //=================================================================================================
	// members
	
    @Autowired
	private ArrowheadService arrowheadService;

	@Autowired
	protected SSLProperties sslProperties;


	
    
	private final Logger logger = LogManager.getLogger( ConsumerMain.class );
    
    //=================================================================================================
	// methods

	//------------------------------------------------------------------------------------------------
    public static void main( final String[] args ) {
    	SpringApplication.run(ConsumerMain.class, args);
    }

    //-------------------------------------------------------------------------------------------------
    @Override
	public void run(final ApplicationArguments args) throws Exception {
		//SIMPLE EXAMPLE OF INITIATING AN ORCHESTRATION

		System.out.println("\n"+"The WM data consumer service has started. Please issue commands in order to trigger an event!");

		final Scanner sc = new Scanner(System.in);

		cmdUI(sc);
	}

	private void cmdUI(final Scanner sc) throws IOException, InterruptedException {
		//final OrchestrationResultDTO orchRes = orchestrate(ProviderCommonConstants.WM_DATA_SERVICE);

		while(true) {
			try {
				System.out.println("Get next data point and store in datamanager? (y/n): ");
				final String answer = sc.nextLine();
				if (!answer.equalsIgnoreCase("y") && !answer.equalsIgnoreCase("n")) {
    				throw new InputMismatchException();
    			}
				if(!answer.equalsIgnoreCase("y")) {
					break;
				}
				final OrchestrationResultDTO orchRes = orchestrate(ProviderCommonConstants.WM_DATA_SERVICE);
				ProviderJSONDTO receivedData = consumeWMData(orchRes);
				//printXML(receivedData);
				final OrchestrationResultDTO orchResDM = orchestrateDM("historian");
				//printJson(receivedData);

				final String address = orchResDM.getProvider().getAddress();
				final int port = orchResDM.getProvider().getPort();
				final String serviceUri = orchResDM.getServiceUri();

				System.out.println("\n"+ address +"\n");
				System.out.println("\n"+ port +"\n");
				System.out.println("\n"+ serviceUri + "\n");
				
				// String registerM = "{\"op\":\"list\"}";
				

				//System.out.println(msg);

				// System.out.print(registerM);

				String senMLMessage = convertToSenML(receivedData);

				//putHistorianData(orchResDM, registerM);
				//putHistorianData(orchResDM, tempSenML());

				putHistorianData(orchResDM, senMLMessage);



			} catch (final InputMismatchException | NumberFormatException ex) {
				System.out.println("Wrong input try again");
				
			} catch (final ArrowheadException ex) {
				System.out.println("Arrowhead Exception!");

			}
		}
		
	}

	private OrchestrationResultDTO orchestrate (final String serviceName) {
		// final ServiceQueryFormDTO srvQDTO = new ServiceQueryFormDTO.Builder(serviceName)
		// 	.interfaces(getInterface()
		// 	.build());
		final ServiceQueryFormDTO srvQDTO = new ServiceQueryFormDTO();
		srvQDTO.setServiceDefinitionRequirement(ProviderCommonConstants.WM_DATA_SERVICE);

		final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();

		final OrchestrationFormRequestDTO orchFormReq = orchestrationFormBuilder.requestedService(srvQDTO).flag(Flag.MATCHMAKING, true).flag(Flag.OVERRIDE_STORE, true).build();

		final OrchestrationResponseDTO orchResp = arrowheadService.proceedOrchestration(orchFormReq);

		if (orchResp == null) {
			logger.info("No orchestration response received");
		} else if (orchResp.getResponse().isEmpty()) {
			logger.info("No provider found");
		}
		else {
		 	final OrchestrationResultDTO orchRes = orchResp.getResponse().get(0);
		 	//validateOrchestrationResult(orchRes, serviceName);
		 	return orchRes;
		}
		throw new ArrowheadException("Unsuccesful orchestration: " + serviceName);
	}

	private OrchestrationResultDTO orchestrateDM (final String serviceName) {
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

	private ProviderJSONDTO consumeWMData (final OrchestrationResultDTO orchRes) {
		final String interfaceName = orchRes.getInterfaces().get(0).getInterfaceName();
		final String secToken = orchRes.getAuthorizationTokens() == null ? null : orchRes.getAuthorizationTokens().get(interfaceName);
		//final String qParam = {orchRes.getMetadata().get(ProviderCommonConstants.REQUEST_PARAM_KEY_NEXT),};

		final HttpMethod httpMethod = HttpMethod.GET;
		final String address = orchRes.getProvider().getAddress();
		final int port = orchRes.getProvider().getPort();
		final String serviceUri = orchRes.getServiceUri();
		

		final ProviderJSONDTO consumedService = arrowheadService.consumeServiceHTTP(ProviderJSONDTO.class, httpMethod, address, port , serviceUri, interfaceName, secToken, null, ProviderCommonConstants.REQUEST_PARAM_KEY_NEXT, ProviderCommonConstants.REQUEST_PARAM_NEXT);
		return consumedService;
		
	}

	private void putHistorianData(final OrchestrationResultDTO orchRes, Object snMLMessage) {
		final String interfaceName = orchRes.getInterfaces().get(0).getInterfaceName();
		final String secToken = orchRes.getAuthorizationTokens() == null ? null : orchRes.getAuthorizationTokens().get(interfaceName);
		//final String qParam = {orchRes.getMetadata().get(ProviderCommonConstants.REQUEST_PARAM_KEY_NEXT),};

		final HttpMethod httpMethod = HttpMethod.PUT;
		final String address = orchRes.getProvider().getAddress();
		final int port = orchRes.getProvider().getPort();
		final String serviceUri = orchRes.getServiceUri() + "/wm_client_consumer/wm-data";
		
		arrowheadService.consumeServiceHTTP(String.class, httpMethod, address, port , serviceUri, interfaceName, secToken, snMLMessage);

	}

	// private void printXML(final Object object) throws IOException {
	// 	final XmlMapper xmlMapper = new XmlMapper();
	// 	xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	// 	System.out.println("\n" + xmlMapper.writeValueAsString(object) + "\n");
		
	
	// }
	// private void printJson(final Object object) throws IOException {
	// 	ObjectMapper objMapper = new ObjectMapper();
	// 	objMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	// 	//System.out.println("\n" + objMapper.writeValueAsString(object)+ "\n");
		
	// }

	private String convertToSenML(ProviderJSONDTO pDTO) throws IOException {
		SenMLAPI snML = SenMLAPI.initJson();
		snML.addRecord(Label.BASE_NAME.attachValue("wm-data"), Label.BASE_TIME.attachValue((double)Instant.now().getEpochSecond()));
		snML.addRecord(Label.NAME.attachValue("Time"), Label.VALUE.attachValue(pDTO.getTimeStamp()));
		snML.addRecord(Label.NAME.attachValue("RPM"),Label.VALUE.attachValue(pDTO.getSpeed()));
		final List<Double> acc = pDTO.getAccelerometer();
		// for(int i = 0; i < 16383; i++) {
                    
		snML.addRecord(Label.NAME.attachValue("acceleration"), Label.VALUE.attachValue(acc.get(0)));
			
		// }
		byte[] json = snML.getSenML();
		System.out.println(new String(json));
		return new String(json);


	}

	// private String tempSenML() throws IOException {
	// 	String msg = "{\"bn\": \"wm-data\", \"bt\" : "+(long)Instant.now().getEpochSecond() + ", \"bu\" : \"rpm\"}, {\"n\": \"speed1\", \"v\": 44.3}";
	// 	SenMLAPI snML = SenMLAPI.initJson();
	// 	snML.addRecord(Label.BASE_NAME.attachValue("wm-data"), Label.BASE_TIME.attachValue((double)Instant.now().getEpochSecond()), Label.BASE_UNIT.attachValue("rpm"));
	// 	snML.addRecord(Label.NAME.attachValue("speed"), Label.VALUE.attachValue(44.3));

	// 	byte [] json = snML.getSenML();
	// 	System.out.println("\n" + new String(json) + "\n");
	// 	return new String(json);

	// }

		
	


    // 	final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
    	
    // 	final ServiceQueryFormDTO requestedService = new ServiceQueryFormDTO();
    // 	requestedService.setServiceDefinitionRequirement("test-service");
    	
    // 	orchestrationFormBuilder.requestedService(requestedService)
    // 							.flag(Flag.MATCHMAKING, false) //When this flag is false or not specified, then the orchestration response cloud contain more proper provider. Otherwise only one will be chosen if there is any proper.
    // 							.flag(Flag.OVERRIDE_STORE, true) //When this flag is false or not specified, then a Store Orchestration will be proceeded. Otherwise a Dynamic Orchestration will be proceeded.
    // 							.flag(Flag.TRIGGER_INTER_CLOUD, false); //When this flag is false or not specified, then orchestration will not look for providers in the neighbor clouds, when there is no proper provider in the local cloud. Otherwise it will. 
    	
    // 	final OrchestrationFormRequestDTO orchestrationRequest = orchestrationFormBuilder.build();
    	
    // 	OrchestrationResponseDTO response = null;
    // 	try {
    // 		response = arrowheadService.proceedOrchestration(orchestrationRequest);			
	// 	} catch (final ArrowheadException ex) {
	// 		//Handle the unsuccessful request as you wish!
	// 	}
    	
    // 	//EXAMPLE OF CONSUMING THE SERVICE FROM A CHOSEN PROVIDER
    	
    // 	if (response == null || response.getResponse().isEmpty()) {
    // 		//If no proper providers found during the orchestration process, then the response list will be empty. Handle the case as you wish!
    // 		logger.debug("Orchestration response is empty");
    // 		return;
    // 	}
    	
    // 	final OrchestrationResultDTO result = response.getResponse().get(0); //Simplest way of choosing a provider.
    	
    // 	final HttpMethod httpMethod = HttpMethod.GET;//Http method should be specified in the description of the service.
    // 	final String address = result.getProvider().getAddress();
    // 	final int port = result.getProvider().getPort();
    // 	final String serviceUri = result.getServiceUri();
    // 	final String interfaceName = result.getInterfaces().get(0).getInterfaceName(); //Simplest way of choosing an interface.
    // 	String token = null;
    // 	if (result.getAuthorizationTokens() != null) {
    // 		token = result.getAuthorizationTokens().get(interfaceName); //Can be null when the security type of the provider is 'CERTIFICATE' or nothing.
	// 	}
    // 	final Object payload = null; //Can be null if not specified in the description of the service.
    	
    // 	final String consumedService = arrowheadService.consumeServiceHTTP(String.class, httpMethod, address, port, serviceUri, interfaceName, token, payload, "testkey", "testvalue");
	// }
}
