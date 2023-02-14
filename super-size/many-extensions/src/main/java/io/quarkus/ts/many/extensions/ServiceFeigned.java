package io.quarkus.ts.many.extensions;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ServiceFeigned {
    public String process(String name) {
        return "Feigned - " + name + " - done";
    }
}
