package io.quarkus.ts.http.restclient.reactive;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

public class RestClientEntity {
    @Max(value = 5, groups = ErrorGroup.class)
    public int number;
    @NotNull
    public String string;

    public RestClientEntity(int number, String string) {
        this.number = number;
        this.string = string;
    }

    public interface ErrorGroup {
    }
}
