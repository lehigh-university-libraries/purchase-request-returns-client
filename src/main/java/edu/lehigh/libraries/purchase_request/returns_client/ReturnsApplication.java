package edu.lehigh.libraries.purchase_request.returns_client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class ReturnsApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ReturnsApplication.class, args);
	}

}
