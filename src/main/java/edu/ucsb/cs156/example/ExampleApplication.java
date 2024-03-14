package edu.ucsb.cs156.example;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import edu.ucsb.cs156.example.services.WiremockService;
import edu.ucsb.cs156.example.wiremock.WiremockWrapper;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class ExampleApplication {

  @Autowired
  WiremockService wiremockService;

  @Profile("wiremock")
  @Bean
  public ApplicationRunner wiremockApplicationRunner() {
    return arg -> {
      log.info("wiremock mode");
      wiremockService.init();
      log.info("wiremockApplicationRunner completed");
    };
  }

  @Profile("development")
  @Bean
  public ApplicationRunner developmentApplicationRunner() {
    return arg -> {
      log.info("development mode");
      log.info("developmentApplicationRunner completed");
    };
  }

  public static void main(String[] args) {
    SpringApplication.run(ExampleApplication.class, args);
  }

  // public static boolean isProfileEnabled(Environment environment, String
  // profile) {
  // String[] profiles = environment.getActiveProfiles();
  // for (String p : profiles) {
  // if (p.equals(profile)) {
  // return true;
  // }
  // }
  // return false;
  // }

  // public static void wireMockConfig(WiremockWrapper wiremockWrapper) {
  // log.info("Wiremock mode");
  // wiremockWrapper.init();
  // }

  // @EventListener(ApplicationReadyEvent.class)
  // public void afterStartup() {
  // log.info("afterStartup: ApplicationReadyEvent fired");

  // // WiremockWrapper wiremockWrapper = new WiremockWrapper();

  // AnnotationConfigApplicationContext context = new
  // AnnotationConfigApplicationContext();
  // Environment environment = context.getEnvironment();

  // log.info("Number of profiles active is: {}",
  // environment.getActiveProfiles().length);

  // log.info("Active profiles: {}",
  // Arrays.toString(environment.getActiveProfiles()));
  // if (isProfileEnabled(environment, "wiremock")) {
  // log.info("Wiremock mode");
  // // wireMockConfig(wiremockWrapper);
  // }
  // context.close();
  // }

}
