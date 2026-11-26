package io.quarkus.ts.spring.data.di;

import static io.quarkus.ts.spring.data.di.ListInjectionBean.LIST_INJECTION_BEAN_ENABLED;
import static io.quarkus.ts.spring.data.di.ListInjectionBean.LIST_INJECTION_BEAN_ENABLED_VALUE;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.quarkus.test.bootstrap.RestService;
import io.quarkus.test.scenarios.QuarkusScenario;
import io.quarkus.test.scenarios.annotations.DisabledOnSemeruJdk;
import io.quarkus.test.services.QuarkusApplication;

@Tag("QUARKUS-2095")
@QuarkusScenario
@DisabledOnSemeruJdk(reason = "OpenJ9 condy bug breaks Quarkus 4 app startup, see eclipse-openj9/openj9#25187 - fix lands in the Jan 2027 Semeru refresh")
public class ListInjectionIT {

    @QuarkusApplication(classes = ListInjectionBean.class)
    static RestService app = new RestService().withProperty(LIST_INJECTION_BEAN_ENABLED, LIST_INJECTION_BEAN_ENABLED_VALUE);

    @Test
    public void appStartsWhenUsingFieldInjectionWithAllAnnotationOnList() {
        Assertions.assertTrue(app.isRunning());
    }
}
