package io.quarkus.ts.security.keycloak;

import static io.quarkus.test.bootstrap.KeycloakService.DEFAULT_REALM;
import static io.quarkus.test.bootstrap.KeycloakService.DEFAULT_REALM_BASE_PATH;
import static io.quarkus.test.bootstrap.KeycloakService.DEFAULT_REALM_FILE;
import static io.quarkus.ts.security.keycloak.TokenUtils.createToken;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import io.quarkus.oidc.Oidc;
import io.quarkus.oidc.OidcTenantConfig;
import io.quarkus.oidc.UserInfo;
import io.quarkus.oidc.runtime.OidcConfig;
import io.quarkus.oidc.runtime.OidcUtils;
import io.quarkus.oidc.runtime.TenantConfigBean;
import io.quarkus.security.PermissionsAllowed;
import io.quarkus.test.bootstrap.KeycloakService;
import io.quarkus.test.bootstrap.RestService;
import io.quarkus.test.scenarios.QuarkusScenario;
import io.quarkus.test.services.KeycloakContainer;
import io.quarkus.test.services.QuarkusApplication;
import io.restassured.RestAssured;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

@QuarkusScenario
public class StaticTenantsIT {

    static final String CLIENT_ID_DEFAULT = "test-application-client";
    static final String CLIENT_SECRET_DEFAULT = "test-application-client-secret";

    @KeycloakContainer(command = { "start-dev", "--import-realm" })
    static KeycloakService keycloak = new KeycloakService(DEFAULT_REALM_FILE, DEFAULT_REALM, DEFAULT_REALM_BASE_PATH);

    @QuarkusApplication
    static RestService app = new RestService()
            .withProperty("quarkus.oidc.auth-server-url", () -> keycloak.getRealmUrl())
            .withProperty("quarkus.oidc.client-id", CLIENT_ID_DEFAULT)
            .withProperty("quarkus.oidc.credentials.secret", CLIENT_SECRET_DEFAULT)
            .withProperties("static-tenants.properties");

    public static KeycloakService getKeycloak() {
        return keycloak;
    }

    @Test
    public void testDefaultTenant() {
        RestAssured.given().auth().oauth2(createToken(keycloak)).get("/user-info/default-tenant-random").then().statusCode(200)
                .body(Matchers.is("test-normal-user"));
    }

    @Test
    public void testNamedTenant() {
        RestAssured.given().auth().oauth2(createToken(keycloak)).get("/user-info/named-tenant-random").then().statusCode(200)
                .body(Matchers.is("test-normal-user"));
    }

    public static class UserInfoEndpoint {
        void observe(@Observes Router router) {
            router.route("/user-info-endpoint").order(1).handler(rc -> rc.response().setStatusCode(200).end("{" +
                    "   \"sub\": \"123456789\"," +
                    "   \"preferred_username\": \"alice\"" +
                    "  }"));
        }
    }

    @Path("user-info")
    public static class UserInfoResource {

        @Inject
        UserInfo userInfo;

        @Inject
        TenantConfigBean tenantConfigBean;

        @Inject
        RoutingContext routingContext;

        @PermissionsAllowed("openid")
        @Path("default-tenant-random")
        @GET
        public String getDefaultTenantName() {
            if (!tenantConfigBean.getDefaultTenant().oidcConfig().authentication().userInfoRequired().orElse(false)) {
                throw new IllegalStateException("Default tenant user info should be required");
            }
            String tenantId = routingContext.get(OidcUtils.TENANT_ID_ATTRIBUTE);
            if (!OidcUtils.DEFAULT_TENANT_ID.equals(tenantId)) {
                throw new IllegalStateException(
                        "Incorrect tenant resolved based on the path - expected default tenant, got " + tenantId);
            }
            // assert tenant path added in the observer method
            assertTenantPathsContain("/extra-default-tenant-path");
            // assert tenant path added in the application.properties
            assertTenantPathsContain("/user-info/default-tenant-random");
            return userInfo.getPreferredUserName();
        }

        @PermissionsAllowed("openid")
        @Path("named-tenant-random")
        @GET
        public String getNamedTenantName() {
            if (!getNamedTenantConfig("named").authentication().userInfoRequired().orElse(false)) {
                throw new IllegalStateException("Named tenant user info should be required");
            }
            String tenantId = routingContext.get(OidcUtils.TENANT_ID_ATTRIBUTE);
            if (!"named".equals(tenantId)) {
                throw new IllegalStateException(
                        "Incorrect tenant resolved based on the path - expected 'named', got " + tenantId);
            }
            assertTenantPathsContain("/user-info/named-tenant-random");
            return userInfo.getPreferredUserName();
        }

        private OidcTenantConfig getNamedTenantConfig(String configName) {
            return tenantConfigBean.getStaticTenant(configName).oidcConfig();
        }

        private void assertTenantPathsContain(String tenantPath) {
            OidcTenantConfig tenantConfig = routingContext.get(OidcTenantConfig.class.getName());
            if (!tenantConfig.tenantPaths().get().contains(tenantPath)) {
                throw new IllegalStateException("Tenant config does not contain the tenant path " + tenantPath);
            }
        }
    }

    public static class OidcStartup {

        void observe(@Observes Oidc oidc, OidcConfig oidcConfig,
                @ConfigProperty(name = "quarkus.http.host") String host,
                @ConfigProperty(name = "quarkus.http.port") String port,
                @ConfigProperty(name = "quarkus.oidc.auth-server-url") String authServerUrl) {
            oidc.create(createDefaultTenant(oidcConfig));
            oidc.create(createNamedTenant(authServerUrl, host, port));
        }

        private static OidcTenantConfig createDefaultTenant(OidcConfig oidcConfig) {
            // this enhances 'application.properties' configuration with a tenant path
            return OidcTenantConfig.builder(OidcConfig.getDefaultTenant(oidcConfig))
                    .tenantPaths("/extra-default-tenant-path")
                    .build();
        }

        private static OidcTenantConfig createNamedTenant(String authServerUrl, String host, String port) {
            return OidcTenantConfig.authServerUrl(authServerUrl)
                    .tenantId("named")
                    .tenantPaths("/user-info/named-tenant-random")
                    .userInfoPath("http://%s:%s/user-info-endpoint".formatted(host, port))
                    .build();
        }
    }
}
