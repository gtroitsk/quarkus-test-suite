package io.quarkus.ts.security.keycloak.oidcclient.extended.restclient;

import static io.quarkus.test.bootstrap.KeycloakService.DEFAULT_REALM;
import static io.quarkus.test.bootstrap.KeycloakService.DEFAULT_REALM_BASE_PATH;
import static io.quarkus.test.bootstrap.KeycloakService.DEFAULT_REALM_FILE;

import io.quarkus.test.bootstrap.KeycloakService;
import io.quarkus.test.scenarios.OpenShiftScenario;
import io.quarkus.test.services.KeycloakContainer;

@OpenShiftScenario
public class OpenShiftDpopIT extends AbstractDpopIT {
    @KeycloakContainer(image = "${rhbk.image}", command = { "start-dev", "--import-realm", "--features=dpop" })
    static KeycloakService keycloak = new KeycloakService(DEFAULT_REALM_FILE, DEFAULT_REALM, DEFAULT_REALM_BASE_PATH);
}
