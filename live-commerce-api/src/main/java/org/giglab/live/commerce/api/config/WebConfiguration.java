package org.giglab.live.commerce.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

  private static final String API_CONTROLLER_PACKAGE = "org.giglab.live.commerce.api.controller";

  @Override
  public void configurePathMatch(PathMatchConfigurer configurer) {
    configurer.addPathPrefix(
        "/api/v1",
        handlerType ->
            handlerType.getPackage() != null
                && handlerType.getPackage().getName().startsWith(API_CONTROLLER_PACKAGE));
  }
}
