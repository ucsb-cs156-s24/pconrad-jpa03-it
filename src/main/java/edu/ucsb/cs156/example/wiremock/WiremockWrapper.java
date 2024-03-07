package edu.ucsb.cs156.example.wiremock;


import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

import lombok.extern.slf4j.Slf4j;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;


@Slf4j
public class WiremockWrapper {
    
    WireMockServer wireMockServer;

    public void init() {
       log.info("WiremockService.init() called");

       wireMockServer = new WireMockServer(options().port(8089)); //No-args constructor will start on port 8080, no HTTPS
       wireMockServer.start();
       
       log.info("WiremockService.init() completed");
    }
}
