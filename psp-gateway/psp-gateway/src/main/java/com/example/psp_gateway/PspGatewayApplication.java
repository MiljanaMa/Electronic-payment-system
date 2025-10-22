package com.example.psp_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PspGatewayApplication {

	public static void main(String[] args) {

		String trustStorePath = PspGatewayApplication.class.getClassLoader().getResource("truststore.jks").getPath();
		System.setProperty("javax.net.ssl.trustStore", trustStorePath);
		System.setProperty("javax.net.ssl.trustStorePassword", "truststorepassword");
		SpringApplication.run(PspGatewayApplication.class, args);
	}

}
