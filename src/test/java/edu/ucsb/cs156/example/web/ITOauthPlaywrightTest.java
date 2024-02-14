package edu.ucsb.cs156.example.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

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
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("integration")
class ITOauthPlaywrightTest {
    @LocalServerPort
    private int port;

    private Browser browser;
    private Page page;

    @RegisterExtension
    static WireMockExtension wme = WireMockExtension.newInstance()
        .options(wireMockConfig()
            .port(8090)
            .extensions(new ResponseTemplateTransformer(true)))
        .build();
    // WireMockServer wireMockServer;
    // @Rule
    // public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8090)
    //     .extensions(new ResponseTemplateTransformer(true)));

    @BeforeEach
    public void setup() {
        // WireMockConfiguration wireMockConfiguration = WireMockConfiguration.options()
        //         .extensions(CaptureStateTransformer.class);

        // wireMockServer = new WireMockServer(options()
        //     .port(8090)
        //     .extensions(CaptureStateTransformer.class));
        // wireMockServer.start();

        // String header = String.format("http://localhost:%d/login/oauth2/code/my-oauth-provider?code=my-acccess-code&state=${state}", port);

        // set up a Mock OAuth server
        wme.stubFor(get(urlPathMatching("/oauth/authorize.*"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "text/html")
                    .withBodyFile("login.html")));

        wme.stubFor(post(urlPathEqualTo("/login"))
                    .willReturn(temporaryRedirect("{{formData request.body 'form' urlDecode=true}}{{{form.redirectUri}}}?code={{{randomValue length=30 type='ALPHANUMERIC'}}}&state={{{form.state}}}")));

        wme.stubFor(post(urlPathEqualTo("/oauth/token"))
                    .willReturn(okJson("{\"token_type\": \"Bearer\",\"access_token\":\"{{randomValue length=20 type='ALPHANUMERIC'}}\"}")));

        wme.stubFor(get(urlPathMatching("/userinfo"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"sub\":\"107126842018026740288\"" +
                                ",\"name\":\"Andrew Peng\"" +
                                ",\"given_name\":\"Andrew\"" +
                                ",\"family_name\":\"Peng\"" +
                                ", \"picture\":\"https://lh3.googleusercontent.com/a/ACg8ocJpOe2SqIpirdIMx7KTj1W4OQ45t6FwpUo40K2V2JON=s96-c\"" +
                                ", \"email\":\"andrewpeng@test.com\"" +
                                ",\"email_verified\":true" +
                                ",\"locale\":\"en\"" +
                                ",\"hd\":\"test.com\"" +
                                "}")
                )
        );

        // wme.stubFor(get(urlPathEqualTo("/userinfo"))
        //         .willReturn(okJson("{\"sub\":\"my-id\",\"email\":\"andrewpeng@ucsb.edu\"}")));

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
    public void tryLogin() throws Exception {
        String url = String.format("http://localhost:%d/oauth2/authorization/my-oauth-provider", port);
        page.navigate(url);
        page.locator("#username").fill("andrewpeng@test.com");
        page.locator("#password").fill("password");
        page.locator("#submit").click();

        // String cURL = page.url();
        // assertSame("http://localhost:8080/", cURL);

        // String cURL = String.format("http://localhost:%d/profile", port);
        // page.navigate(cURL);

        String bodyHTML = page.innerHTML("body");
        String expectedHTML = StringSource.getIntegrationDefaultLocalhostContent();
        assertEquals(expectedHTML, bodyHTML);
    }

}