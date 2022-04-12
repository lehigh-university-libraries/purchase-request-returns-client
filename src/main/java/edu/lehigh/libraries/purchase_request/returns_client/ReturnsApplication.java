package edu.lehigh.libraries.purchase_request.returns_client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@ConditionalOnWebApplication
@Slf4j
public class ReturnsApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ReturnsApplication.class);
	}

	public static void main(String[] args) throws Exception {
		log.info("Starting the Returns Application");
		SpringApplication.run(ReturnsApplication.class, args);
	}

}
