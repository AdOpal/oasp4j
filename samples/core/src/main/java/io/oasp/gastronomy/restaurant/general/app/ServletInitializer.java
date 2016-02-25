package io.oasp.gastronomy.restaurant.general.app;

import io.oasp.gastronomy.restaurant.SpringBootApp;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

    return application.sources(SpringBootApp.class);
  }
}