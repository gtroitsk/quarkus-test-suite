package io.quarkus.ts.hibernate.reactive;

import org.junit.jupiter.api.Disabled;

import io.quarkus.test.bootstrap.OracleService;
import io.quarkus.test.bootstrap.RestService;
import io.quarkus.test.scenarios.QuarkusScenario;
import io.quarkus.test.services.Container;
import io.quarkus.test.services.QuarkusApplication;

// TODO: enable with next Hibernate Reactive bump in Quarkus main
@Disabled("https://github.com/quarkusio/quarkus/issues/32102#issuecomment-1482501348")
@QuarkusScenario
public class OracleDatabaseIT extends AbstractDatabaseHibernateReactiveIT {

    private static final String ORACLE_USER = "quarkus_test";
    private static final String ORACLE_PASSWORD = "quarkus_test";
    private static final String ORACLE_DATABASE = "quarkus_test";
    private static final int ORACLE_PORT = 1521;

    @Container(image = "${oracle.image}", port = ORACLE_PORT, expectedLog = "DATABASE IS READY TO USE!")
    static OracleService database = new ProvisionalOracleService()
            .with(ORACLE_USER, ORACLE_PASSWORD, ORACLE_DATABASE);

    @QuarkusApplication
    static RestService app = new RestService().withProperties("oracle.properties")
            .withProperty("quarkus.datasource.username", ORACLE_USER)
            .withProperty("quarkus.datasource.password", ORACLE_PASSWORD)
            .withProperty("quarkus.datasource.reactive.url", database::getReactiveUrl);

    @Override
    protected RestService getApp() {
        return app;
    }

    private static class ProvisionalOracleService extends OracleService {
        @Override
        public String getReactiveUrl() {
            return getURI().withScheme(getJdbcName() + ":thin:@").withPath("/" + getDatabase()).toString();
        }
    }
}
