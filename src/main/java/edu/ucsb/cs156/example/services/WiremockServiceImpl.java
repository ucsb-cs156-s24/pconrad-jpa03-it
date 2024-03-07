package edu.ucsb.cs156.example.services;

import edu.ucsb.cs156.example.models.SystemInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.temporaryRedirect;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

// This class relies on property values
// For hints on testing, see: https://www.baeldung.com/spring-boot-testing-configurationproperties

@Slf4j
@Service("wiremockService")
@Profile("wiremock")
@ConfigurationProperties
public class WiremockServiceImpl extends WiremockService {

  WireMockServer wireMockServer;

  public WireMockServer getWiremockServer() {
    return wireMockServer;
  }

  public void init() {
    log.info("WiremockServiceImpl.init() called");

    WireMockServer wireMockServer = new WireMockServer(options().port(8090)); // No-args constructor will start on port
                                                                              // 8080, no HTTPS

    wireMockServer.stubFor(get(urlPathMatching("/oauth/authorize.*"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "text/html")
            .withBodyFile("login.html")));

    wireMockServer.stubFor(post(urlPathEqualTo("/login"))
        .willReturn(temporaryRedirect(
            "{{formData request.body 'form' urlDecode=true}}{{{form.redirectUri}}}?code={{{randomValue length=30 type='ALPHANUMERIC'}}}&state={{{form.state}}}")));

    wireMockServer.stubFor(post(urlPathEqualTo("/oauth/token"))
        .willReturn(
            okJson("{\"token_type\": \"Bearer\",\"access_token\":\"{{randomValue length=20 type='ALPHANUMERIC'}}\"}")));

    wireMockServer.stubFor(get(urlPathMatching("/userinfo"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("{\"sub\":\"107126842018026740288\"" +
                ",\"name\":\"Andrew Peng\"" +
                ",\"given_name\":\"Andrew\"" +
                ",\"family_name\":\"Peng\"" +
                ", \"picture\":\"https://lh3.googleusercontent.com/a/ACg8ocJpOe2SqIpirdIMx7KTj1W4OQ45t6FwpUo40K2V2JON=s96-c\""
                +
                ", \"email\":\"andrewpeng@ucsb.edu\"" +
                ",\"email_verified\":true" +
                ",\"locale\":\"en\"" +
                ",\"hd\":\"ucsb.edu\"" +
                "}")));

    wireMockServer.start();

    log.info("WiremockServiceImpl.init() completed");
  }
}
