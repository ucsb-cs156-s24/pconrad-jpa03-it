package edu.ucsb.cs156.example.web;

import static org.junit.Assert.assertThat;
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
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;

import com.microsoft.playwright.Playwright;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import edu.ucsb.cs156.example.CaptureStateTransformer;
import edu.ucsb.cs156.example.helpers.StringSource;
import edu.ucsb.cs156.example.services.WiremockServiceImpl;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@EnableRuleMigrationSupport
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("integration")
class ITOauthPlaywright {
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
    // public WireMockRule wireMockRule = new
    // WireMockRule(wireMockConfig().port(8090)
    // .extensions(new ResponseTemplateTransformer(true)));

    @BeforeEach
    public void setup() {
        // WireMockConfiguration wireMockConfiguration = WireMockConfiguration.options()
        // .extensions(CaptureStateTransformer.class);

        // wireMockServer = new WireMockServer(options()
        // .port(8090)
        // .extensions(CaptureStateTransformer.class));
        // wireMockServer.start();

        // String header =
        // String.format("http://localhost:%d/login/oauth2/code/my-oauth-provider?code=my-acccess-code&state=${state}",
        // port);

        // set up a Mock OAuth server

        WiremockServiceImpl.setupMocks(wme);

        // browser = Playwright.create().chromium().launch();

        browser = Playwright.create().chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));

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
        page.locator("#username").fill("andrewpeng@ucsb.edu");
        page.locator("#password").fill("password");
        page.locator("#submit").click();

        assertThat(page.getByText("Log Out")).isVisible();
        assertThat(page.getByText("Welcome, andrewpeng@ucsb.edu")).isVisible();
    }

}