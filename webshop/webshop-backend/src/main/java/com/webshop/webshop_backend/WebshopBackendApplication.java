package com.webshop.webshop_backend;

import com.webshop.webshop_backend.config.RsaKeyConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RsaKeyConfigProperties.class)
@SpringBootApplication
public class WebshopBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebshopBackendApplication.class, args);
	}

}
