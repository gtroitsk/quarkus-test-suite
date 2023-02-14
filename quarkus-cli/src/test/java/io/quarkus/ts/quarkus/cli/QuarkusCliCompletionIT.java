package io.quarkus.ts.quarkus.cli;

import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

import io.quarkus.test.bootstrap.QuarkusCliClient;
import io.quarkus.test.scenarios.QuarkusScenario;

@Tag("QUARKUS-960")
@Tag("quarkus-cli")
@QuarkusScenario
@DisabledIfSystemProperty(named = "profile.id", matches = "native", disabledReason = "Only for JVM verification")
public class QuarkusCliCompletionIT {

    static final String EXPECTED_COMPLETION_OUTPUT = "Generates completions for the options and subcommands";

    @Inject
    static QuarkusCliClient cliClient;

    @Test
    public void shouldConfigureCompletion() {
        QuarkusCliClient.Result result = cliClient.run("completion");

        assertTrue(result.isSuccessful(), "Completion command failed: " + result.getOutput());
        assertTrue(result.getOutput().contains(EXPECTED_COMPLETION_OUTPUT), "Unexpected output: " + result.getOutput());
    }
}
