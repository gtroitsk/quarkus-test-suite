package io.quarkus.ts.security.keycloak;

import static io.quarkus.test.bootstrap.KeycloakService.DEFAULT_REALM;
import static io.quarkus.test.bootstrap.KeycloakService.DEFAULT_REALM_BASE_PATH;
import static io.quarkus.test.bootstrap.KeycloakService.DEFAULT_REALM_FILE;
import static io.quarkus.ts.security.keycloak.BaseOidcSecurityIT.CLIENT_ID_DEFAULT;
import static io.quarkus.ts.security.keycloak.BaseOidcSecurityIT.CLIENT_SECRET_DEFAULT;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.bootstrap.KeycloakService;
import io.quarkus.test.bootstrap.RestService;
import io.quarkus.test.scenarios.QuarkusScenario;
import io.quarkus.test.services.KeycloakContainer;
import io.quarkus.test.services.QuarkusApplication;

@QuarkusScenario
public class OidcRevocationFlowIT {

    @KeycloakContainer(command = { "start-dev", "--import-realm" })
    static KeycloakService keycloak = new KeycloakService(DEFAULT_REALM_FILE, DEFAULT_REALM, DEFAULT_REALM_BASE_PATH);

    @QuarkusApplication
    static RestService app = new RestService()
            .withProperty("quarkus.oidc.auth-server-url", () -> keycloak.getRealmUrl())
            .withProperty("quarkus.oidc.client-id", CLIENT_ID_DEFAULT)
            .withProperty("quarkus.oidc.credentials.secret", CLIENT_SECRET_DEFAULT)
            .withProperty("quarkus.oidc.token.refresh-enabled", "true");

    protected KeycloakService getKeycloak() {
        return keycloak;
    }

    @Test
    public void shouldLogoutAndRevokeTokens() {
        // Simulate auth via frontend client, then call /oidc-revoke
        String accessToken = createToken("test-normal-user", "test-normal-user");

        given()
                .auth().oauth2(accessToken)
                .when()
                .get("/revoke")
                .then()
                .statusCode(200)
                .body(is("You are logged out"));
    }

    private static String createToken(String username, String password) {
        return keycloak.createAuthzClient(CLIENT_ID_DEFAULT, CLIENT_SECRET_DEFAULT)
                .obtainAccessToken(username, password).getToken();
    }
}
