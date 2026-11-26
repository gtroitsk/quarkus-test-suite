package io.quarkus.ts.reactive.rest.data.panache.secured;

import static io.restassured.RestAssured.given;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.quarkus.test.scenarios.QuarkusScenario;
import io.quarkus.test.scenarios.annotations.DisabledOnSemeruJdk;

@Tag("QUARKUS-7179")
@QuarkusScenario
@DisabledOnSemeruJdk(reason = "OpenJ9 condy bug breaks Quarkus 4 app startup, see eclipse-openj9/openj9#25187 - fix lands in the Jan 2027 Semeru refresh")
public class PermissionsAllowedPanacheEntityResourceIT extends AbstractPermissionsAllowedPanacheResourceIT {
    private static final String BASE_URL = "/secured/entity";

    @Override
    protected String getBaseUrl() {
        return BASE_URL;
    }

    @Test
    void customMethodPermissionsAllowedNoAuth() {
        given()
                .get(getBaseUrl() + "/permissions-allowed-method/custom-count")
                .then().statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    void customMethodPermissionsAllowedForbidden() {
        given()
                .auth().basic("user", "user")
                .get(getBaseUrl() + "/permissions-allowed-method/custom-count")
                .then().statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    void customMethodPermissionsAllowedPermitted() {
        given()
                .auth().basic("admin", "admin")
                .get(getBaseUrl() + "/permissions-allowed-method/custom-count")
                .then().statusCode(HttpStatus.SC_OK);
    }
}
