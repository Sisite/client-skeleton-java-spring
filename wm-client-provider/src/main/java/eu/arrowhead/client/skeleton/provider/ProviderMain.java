package eu.arrowhead.client.skeleton.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import eu.arrowhead.common.CommonConstants;


import eu.arrowhead.client.skeleton.common.ProviderCommonConstants;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, ProviderCommonConstants.BASE_PACKAGE, "eu.arrowhead.client.skeleton.provider.controller.WMDataService"}) //TODO: add custom packages if any
public class ProviderMain {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static void main(final String[] args) {
		SpringApplication.run(ProviderMain.class, args);
	}	
}
