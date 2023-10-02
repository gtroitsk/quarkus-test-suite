package io.quarkus.ts.micrometer.prometheus.kafka.reactive;

import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import io.quarkus.test.bootstrap.KafkaService;
import io.quarkus.test.bootstrap.RestService;
import io.quarkus.test.scenarios.OpenShiftScenario;
import io.quarkus.test.services.KafkaContainer;
import io.quarkus.test.services.QuarkusApplication;

@OpenShiftScenario
@DisabledIfSystemProperty(named = "ts.arm.missing.services.excludes", matches = "true", disabledReason = "https://github.com/quarkus-qe/quarkus-test-suite/issues/1144")
@DisabledIfSystemProperty(named = "ts.s390x.missing.services.excludes", matches = "true", disabledReason = "debezium/zookeeper container not available on s390x.")
@EnabledIfSystemProperty(named = "ts.redhat.registry.enabled", matches = "true")
public class OpenShiftAmqStreamsAlertEventsReactiveIT extends BaseOpenShiftAlertEventsReactiveIT {

    @KafkaContainer(image = "${amq-streams.image}", version = "${amq-streams.version}")
    static KafkaService kafka = new KafkaService();

    @QuarkusApplication
    static RestService app = new RestService()
            .withProperty("kafka.bootstrap.servers", kafka::getBootstrapUrl)
            .onPostStart(OpenShiftAmqStreamsAlertEventsReactiveIT::loadServiceMonitor);

    @Override
    protected RestService getApp() {
        return app;
    }
}
