package org.giglab.live.commerce.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.giglab.live.commerce")
public class LiveCommerceApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(LiveCommerceApiApplication.class, args);
  }
}
