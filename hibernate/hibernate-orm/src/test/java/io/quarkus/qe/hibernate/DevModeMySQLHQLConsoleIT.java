package io.quarkus.qe.hibernate;

import java.util.Map;

import org.junit.jupiter.api.Tag;

import io.quarkus.test.bootstrap.DevModeQuarkusService;
import io.quarkus.test.scenarios.QuarkusScenario;
import io.quarkus.test.scenarios.annotations.DisabledOnSemeruJdk;
import io.quarkus.test.services.Dependency;
import io.quarkus.test.services.DevModeQuarkusApplication;
import io.smallrye.common.os.OS;

@Tag("fips-incompatible") // TODO: enable when https://github.com/quarkusio/quarkus/issues/40526 gets fixed
@QuarkusScenario
@Tag("QUARKUS-6243")
@DisabledOnSemeruJdk(reason = "OpenJ9 condy bug breaks Quarkus 4 app startup, see eclipse-openj9/openj9#25187 - fix lands in the Jan 2027 Semeru refresh")
public class DevModeMySQLHQLConsoleIT extends AbstractHQLConsoleIT {

    @DevModeQuarkusApplication(dependencies = @Dependency(artifactId = "quarkus-jdbc-mysql"))
    static DevModeQuarkusService app = (DevModeQuarkusService) new DevModeQuarkusService()
            .withProperties("mysql.properties")
            .withProperties(() -> {
                if (OS.WINDOWS.isCurrent() && DockerUtils.isNotPodman()) {
                    String dockerIp = System.getenv(DockerUtils.DOCKER_IP);
                    if (dockerIp != null && !dockerIp.isEmpty()) {
                        // container IP in Minikube is not localhost
                        return Map.of("quarkus.datasource.dev-ui.allowed-db-host", dockerIp);
                    }
                }
                return Map.of();
            });
}
