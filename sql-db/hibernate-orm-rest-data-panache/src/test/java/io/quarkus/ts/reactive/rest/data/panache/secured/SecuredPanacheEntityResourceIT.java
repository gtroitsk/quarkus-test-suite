package io.quarkus.ts.reactive.rest.data.panache.secured;

import org.junit.jupiter.api.Tag;

import io.quarkus.test.scenarios.QuarkusScenario;
import io.quarkus.test.scenarios.annotations.DisabledOnSemeruJdk;

@Tag("QUARKUS-2788")
@QuarkusScenario
@DisabledOnSemeruJdk(reason = "OpenJ9 condy bug breaks Quarkus 4 app startup, see eclipse-openj9/openj9#25187 - fix lands in the Jan 2027 Semeru refresh")
public class SecuredPanacheEntityResourceIT extends AbstractSecuredPanacheResourceIT {
    private static final String BASE_URL = "/secured/entity";

    @Override
    protected String getBaseUrl() {
        return BASE_URL;
    }
}
