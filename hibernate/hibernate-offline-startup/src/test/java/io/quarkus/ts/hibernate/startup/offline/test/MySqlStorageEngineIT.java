package io.quarkus.ts.hibernate.startup.offline.test;

import io.quarkus.test.bootstrap.RestService;
import io.quarkus.test.scenarios.QuarkusScenario;
import io.quarkus.test.scenarios.annotations.DisabledOnSemeruJdk;
import io.quarkus.test.services.Dependency;
import io.quarkus.test.services.QuarkusApplication;

@QuarkusScenario
@DisabledOnSemeruJdk(reason = "OpenJ9 condy bug breaks Quarkus 4 app startup, see eclipse-openj9/openj9#25187 - fix lands in the Jan 2027 Semeru refresh")
public class MySqlStorageEngineIT extends AbstractStorageEngineIT {

    @QuarkusApplication(dependencies = @Dependency(artifactId = "quarkus-jdbc-mysql"), properties = "storage-engine-test.properties")
    static final RestService app = new RestService()
            .withProperty("quarkus.datasource.db-kind", "mysql")
            .withProperty("jdbc-url", "jdbc:mysql://localhost:3306");

}