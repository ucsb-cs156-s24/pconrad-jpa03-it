package edu.ucsb.cs156.example.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import edu.ucsb.cs156.example.CaptureStateTransformer;
import edu.ucsb.cs156.example.helpers.StringSource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@EnableRuleMigrationSupport
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
class ITOauthPlaywrightTest {
    @LocalServerPort
    private int port;

    private Browser browser;
    private Page page;

    // @RegisterExtension
    // static WireMockExtension wme = WireMockExtension.newInstance()
    //     .options(wireMockConfig()
    //         .port(8090)
    //         .extensions(CaptureStateTransformer.class))
    //     .build();
    // WireMockServer wireMockServer;
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8090)
        .extensions(CaptureStateTransformer.class));

    @BeforeEach
    public void setup() {
        // WireMockConfiguration wireMockConfiguration = WireMockConfiguration.options()
        //         .extensions(CaptureStateTransformer.class);

        // wireMockServer = new WireMockServer(options()
        //     .port(8090)
        //     .extensions(CaptureStateTransformer.class));
        // wireMockServer.start();

        String header = String.format("http://localhost:%d/login/oauth2/code/my-oauth-client?code=my-acccess-code&state=${state}", port);

        // set up a Mock OAuth server
        stubFor(get(urlPathMatching("/oauth/authorize.*"))
                .willReturn(aResponse()
                        .withStatus(302)
                        .withHeader("Location",header)
                        .withTransformers("CaptureStateTransformer")
                )
        );

        stubFor(post(urlPathMatching("/oauth/token"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"access_token\":\"my-access-token\"" +
                                ", \"token_type\":\"Bearer\"" +
                                ", \"expires_in\":\"3600\"" +
                                "}")
                )
        );

        stubFor(get(urlPathMatching("/userinfo"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"sub\":\"my-user-id\"" +
                                ",\"name\":\"Mark Hoogenboom\"" +
                                ", \"email\":\"mark.hoogenboom@example.com\"" +
                                "}")
                )
        );

        browser = Playwright.create().chromium().launch();
        BrowserContext context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    public void teardown() {
        // wireMockServer.stop();

        browser.close();
    }

    @Test
    public void testGreeting() throws Exception {
        // String url = String.format("http://localhost:%d/", port);
        // page.navigate(url);
        // String bodyHTML = page.innerHTML("body");
        // String expectedHTML = StringSource.getDevelopmentDefaultLocalhostContent();
        // assertEquals(expectedHTML, bodyHTML);

        String url = String.format("http://localhost:%d/oauth2/authorization/my-oauth-provider", port);
        page.navigate(url);
        // page.getByText("Log In").click();
        // url = String.format("http://localhost:%d/", port);
        // page.navigate(url);
        String bodyHTML = page.innerHTML("body");
        String expectedHTML = StringSource.getIntegrationDefaultLocalhostContent();
        assertEquals(expectedHTML, bodyHTML);
    }

}