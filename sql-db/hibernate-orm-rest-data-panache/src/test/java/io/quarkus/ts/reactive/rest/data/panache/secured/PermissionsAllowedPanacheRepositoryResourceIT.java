package io.quarkus.ts.reactive.rest.data.panache.secured;

import org.junit.jupiter.api.Tag;

import io.quarkus.test.scenarios.QuarkusScenario;
import io.quarkus.test.scenarios.annotations.DisabledOnSemeruJdk;

@Tag("QUARKUS-7179")
@QuarkusScenario
@DisabledOnSemeruJdk(reason = "OpenJ9 condy bug breaks Quarkus 4 app startup, see eclipse-openj9/openj9#25187 - fix lands in the Jan 2027 Semeru refresh")
public class PermissionsAllowedPanacheRepositoryResourceIT extends AbstractPermissionsAllowedPanacheResourceIT {
    private static final String BASE_URL = "/secured/repository";

    @Override
    protected String getBaseUrl() {
        return BASE_URL;
    }
}
