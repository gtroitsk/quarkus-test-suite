package io.quarkus.ts.sqldb.sqlapp;

import io.quarkus.test.bootstrap.RestService;
import io.quarkus.test.scenarios.QuarkusScenario;
import io.quarkus.test.scenarios.annotations.DisabledOnNative;
import io.quarkus.test.scenarios.annotations.DisabledOnSemeruJdk;
import io.quarkus.test.services.QuarkusApplication;

@DisabledOnNative(reason = "Works in native, but we only provide DEV support")
@QuarkusScenario
@DisabledOnSemeruJdk(reason = "OpenJ9 condy bug breaks Quarkus 4 app startup, see eclipse-openj9/openj9#25187 - fix lands in the Jan 2027 Semeru refresh")
public class H2SqlDatabaseIT extends AbstractSqlDatabaseIT {

    @QuarkusApplication
    static RestService app = new RestService().withProperties("h2.properties");
}
