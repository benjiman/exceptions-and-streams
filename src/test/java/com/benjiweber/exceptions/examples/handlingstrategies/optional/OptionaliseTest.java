package com.benjiweber.exceptions.examples.handlingstrategies.optional;

import org.junit.Test;

import java.util.Objects;
import java.util.Optional;

import static com.benjiweber.exceptions.examples.handlingstrategies.optional.Optionalise.optionalise;
import static org.junit.Assert.*;

public class OptionaliseTest {

    @Test
    public void should_wrap_result_in_optional() {
        Optional<String> result = optionalise(this::operationThatThrows)
            .apply("DoNotThrow");
        assertEquals(Optional.of("SuccessfulResult"), result);
    }

    @Test
    public void should_map_exception_to_empty_optional() {
        Optional<String> result = optionalise(this::operationThatThrows)
                .apply("Throw");
        assertEquals(Optional.empty(), result);
    }

    public String operationThatThrows(String input) throws ACheckedException {
        if (Objects.equals("DoNotThrow", input)) {
            return "SuccessfulResult";
        }
        throw new ACheckedException();
    }

    public static class ACheckedException extends Exception {}
}