package io.quarkus.ts.http.restclient.reactive;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.quarkus.test.bootstrap.RestService;
import io.quarkus.test.scenarios.QuarkusScenario;
import io.quarkus.test.services.QuarkusApplication;

@QuarkusScenario
@Tag("QUARKUS-6262")
public class RestClientConstraintsOverrideIT {

    @QuarkusApplication
    static RestService app = new RestService();

    @Test
    public void fetchDefault() {
        app.given().get("/hibernate-validator/test-rest-client")
                .then()
                .statusCode(200);
    }
}
