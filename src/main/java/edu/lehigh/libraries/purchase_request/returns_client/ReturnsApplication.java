package edu.lehigh.libraries.purchase_request.returns_client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@ConditionalOnWebApplication
@Slf4j
public class ReturnsApplication {

	public static void main(String[] args) throws Exception {
		log.info("Starting the Returns Application");
		SpringApplication.run(ReturnsApplication.class, args);
	}

}
