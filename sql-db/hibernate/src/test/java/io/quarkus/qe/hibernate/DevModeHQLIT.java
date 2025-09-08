package io.quarkus.qe.hibernate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import io.quarkus.test.bootstrap.DevModeQuarkusService;
import io.quarkus.test.bootstrap.Protocol;
import io.quarkus.test.scenarios.QuarkusScenario;
import io.quarkus.test.services.DevModeQuarkusApplication;

@QuarkusScenario
public class DevModeHQLIT extends DevUIJsonRPCTest {

    private String expectedPersistenceUnitName;
    private String expectedTableName;
    private String expectedClassName;
    private Integer expectedResults;

    public DevModeHQLIT() {
        super("quarkus-hibernate-orm");
    }

    @DevModeQuarkusApplication
    static DevModeQuarkusService app = (DevModeQuarkusService) new DevModeQuarkusService()
            .withProperty("quarkus.hibernate-orm.dev-ui.allow-hql", "true");

    @Test
    @Disabled
    public void testExecuteHQL() throws Exception {
        expectedPersistenceUnitName = "<default>";
        expectedTableName = "Item";
        expectedClassName = "io.quarkus.qe.hibernate.items.Item";
        expectedResults = 1;
        String entityName = expectedTableName != null ? expectedTableName : "Item";
        Map<String, Object> arguments = Map.of(
                "query", "select e from " + entityName + " e where e.id = 1",
                "persistenceUnit", expectedPersistenceUnitName != null ? expectedPersistenceUnitName : "",
                "pageNumber", 1,
                "pageSize", 15);

        JsonNode dataSet = super.executeJsonRPCMethod("executeHQL", arguments);

        if (expectedResults != null) {
            // Expect number of results
            assertNotNull(dataSet);
            assertTrue(dataSet.has("resultCount"));
            assertTrue(dataSet.has("data"));
            assertFalse(dataSet.has("error"));

            JsonNode elements = dataSet.get("resultCount");
            assertTrue(elements.isNumber());
            assertEquals(expectedResults, elements.intValue());

            JsonNode data = dataSet.get("data");
            assertTrue(data.isArray());
            assertEquals(expectedResults, data.size());
            for (int i = 1; i <= expectedResults; i++) {
                JsonNode element = data.get(i - 1);
                assertEquals(i, element.get("id").intValue());
                assertEquals("entity_" + i, data.get(0).get("field").textValue());
            }
        } else if (expectedPersistenceUnitName != null) {
            // Expecting an empty result set
            assertNotNull(dataSet);
            assertTrue(dataSet.has("resultCount"));
            assertTrue(dataSet.has("data"));
            assertFalse(dataSet.has("error"));

            JsonNode elements = dataSet.get("resultCount");
            assertTrue(elements.isNumber());
            assertEquals(0, elements.intValue());

            JsonNode data = dataSet.get("data");
            assertTrue(data.isArray());
            assertEquals(0, data.size());
        } else {
            // Expecting an error
            assertNotNull(dataSet);
            assertTrue(dataSet.has("resultCount"));
            assertFalse(dataSet.has("data"));
            assertTrue(dataSet.has("error"));

            JsonNode elements = dataSet.get("resultCount");
            assertTrue(elements.isNumber());
            assertEquals(-1, elements.intValue());

            JsonNode error = dataSet.get("error");
            assertTrue(error.isTextual());
            assertTrue(error.asText().contains("No such persistence unit"));
        }
    }

    @Test
    public void testHqlConsoleQueries() throws InterruptedException {
        try (Playwright playwright = Playwright.create()) {
            try (Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(true))) {
                BrowserContext context = browser.newContext();
                Page page = context.newPage();

                String hqlConsoleUrl = app
                        .getURI(Protocol.HTTP)
                        .withPath("/q/dev-ui/quarkus-hibernate-orm/hql-console")
                        .toString();
                page.navigate(hqlConsoleUrl);

                Thread.sleep(10000000000L);

                // Example 1: Run a valid query
                runHqlQuery(page, "from Item");

                // Check that results table appears
                Locator resultsTable = page.locator("vaadin-grid"); // results are usually in a vaadin grid
                assertTrue(resultsTable.isVisible(), "Expected results table to be visible for valid query");

                //                // Example 2: Run an invalid query
                //                runHqlQuery(page, "select invalid from DoesNotExist");
                //
                //                // Check that error message appears
                //                Locator errorBox = page.locator("div[slot='message']");
                //                assertTrue(errorBox.isVisible(), "Expected error message to be shown for invalid query");
                //                assertTrue(errorBox.innerText().contains("could not resolve property")
                //                                || errorBox.innerText().contains("entity not found"),
                //                        "Expected error text, but got: " + errorBox.innerText());
            }
        }
    }

    private void runHqlQuery(Page page, String query) {
        // Find the query input box
        Locator queryInput = page.locator("textarea[placeholder='Enter an HQL query']");
        queryInput.fill(query);

        // Click the blue run button (play icon)
        Locator runButton = page.locator("button:has-text('▶')").first();
        runButton.click();

        // Wait for either results or error
        page.waitForTimeout(1000); // give it a moment, adjust if needed
    }

    //    private static void assertOnGrpcServicePage(Consumer<Page> assertion) {
    //        try (Playwright playwright = Playwright.create()) {
    //            try (Browser browser = playwright.firefox().launch()) {
    //                Page page = browser.newContext().newPage();
    //                var grpcServicesViewUrl = app
    //                        .getURI(Protocol.HTTP)
    //                        .withPath("/q/dev-ui/quarkus-grpc/services")
    //                        .toString();
    //                page.navigate(grpcServicesViewUrl);
    //
    //                untilAsserted(() -> assertion.accept(page));
    //            }
    //        }
    //    }
}
