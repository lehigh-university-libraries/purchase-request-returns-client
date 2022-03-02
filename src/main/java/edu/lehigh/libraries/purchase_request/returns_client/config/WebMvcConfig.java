package edu.lehigh.libraries.purchase_request.returns_client.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
      .addResourceHandler("/resources/css/*")
      .addResourceLocations("classpath:/static/css/");
    registry
      .addResourceHandler("/resources/js/*")
      .addResourceLocations("classpath:/static/js/");
  }

}
