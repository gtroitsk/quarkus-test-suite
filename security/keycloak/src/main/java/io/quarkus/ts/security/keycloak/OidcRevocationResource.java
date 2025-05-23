package io.quarkus.ts.security.keycloak;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import io.quarkus.oidc.AccessTokenCredential;
import io.quarkus.oidc.OidcProviderClient;
import io.quarkus.oidc.OidcSession;
import io.quarkus.oidc.RefreshToken;

@Path("/revoke")
public class OidcRevocationResource {

    @Inject
    OidcSession oidcSession;

    @Inject
    OidcProviderClient oidcProviderClient;

    @Inject
    AccessTokenCredential accessToken;

    @Inject
    RefreshToken refreshToken;

    @GET
    public String logout() {
        String access = accessToken != null ? accessToken.getToken() : "null";
        String refresh = refreshToken != null ? refreshToken.getToken() : "null";

        System.out.println("Access Token: " + access);
        System.out.println("Refresh Token: " + refresh);

        return oidcSession.logout()
                .chain(() -> oidcProviderClient.revokeAccessToken(accessToken.getToken()))
                .chain(() -> oidcProviderClient.revokeRefreshToken(refreshToken.getToken()))
                .map((result) -> "You are logged out")
                .await().indefinitely();
    }
}
