package eu.arrowhead.client.skeleton.consumer;


import java.io.IOException;
import java.util.InputMismatchException;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.CommonConstants;

import eu.arrowhead.common.dto.shared.OrchestrationFlags.Flag;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO.Builder;
import eu.arrowhead.common.dto.shared.OrchestrationResponseDTO;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.common.dto.shared.ServiceQueryFormDTO;
import eu.arrowhead.common.exception.ArrowheadException;

import eu.arrowhead.client.library.ArrowheadService;

import eu.arrowhead.client.skeleton.common.ProviderCommonConstants;
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
				//Orchestrate and consume the service
				final OrchestrationResultDTO orchRes = orchestrate(ProviderCommonConstants.WM_DATA_SERVICE);
				ProviderJSONDTO receivedData = consumeWMData(orchRes);

				//Print the received data
				printJson(receivedData);

			} catch (final InputMismatchException | NumberFormatException ex) {
				System.out.println("Wrong input try again");
				
			} catch (final ArrowheadException ex) {
				System.out.println("Arrowhead Exception!");

			}
		}
		
	}

	//Orchestrate request for wm-data service

	private OrchestrationResultDTO orchestrate (final String serviceName) {

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

	//Consume the data from the provider

	private ProviderJSONDTO consumeWMData (final OrchestrationResultDTO orchRes) {
		final String interfaceName = orchRes.getInterfaces().get(0).getInterfaceName();
		final String secToken = orchRes.getAuthorizationTokens() == null ? null : orchRes.getAuthorizationTokens().get(interfaceName);

		final HttpMethod httpMethod = HttpMethod.GET;
		final String address = orchRes.getProvider().getAddress();
		final int port = orchRes.getProvider().getPort();
		final String serviceUri = orchRes.getServiceUri();
		

		final ProviderJSONDTO consumedService = arrowheadService.consumeServiceHTTP(ProviderJSONDTO.class, httpMethod, address, port , serviceUri, interfaceName, secToken, null, ProviderCommonConstants.REQUEST_PARAM_KEY_NEXT, ProviderCommonConstants.REQUEST_PARAM_NEXT);
		return consumedService;
		
	}

	//Print consumed data
	private void printJson(final Object object) throws IOException {
		ObjectMapper objMapper = new ObjectMapper();
		objMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		System.out.println("\n" + objMapper.writeValueAsString(object)+ "\n");
		
	}



		
	



}
