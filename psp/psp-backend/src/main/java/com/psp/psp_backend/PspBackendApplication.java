package com.psp.psp_backend;

import com.psp.psp_backend.config.RsaKeyConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RsaKeyConfigProperties.class)
@SpringBootApplication
public class PspBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PspBackendApplication.class, args);
	}

}
