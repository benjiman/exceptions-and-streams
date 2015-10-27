package com.benjiweber.exceptions.examples.handlingstrategies.runtime;

import com.benjiweber.exceptions.examples.handlingstrategies.either.EitheriseTest;
import org.junit.Test;

import java.util.Objects;

import static com.benjiweber.exceptions.examples.handlingstrategies.runtime.Unchecked.unchecked;
import static org.junit.Assert.*;

public class UncheckedTest {

    @Test
    public void result_should_be_unaffected() {
        String result = unchecked(this::operationThatThrows)
            .apply("DoNotThrow");
        assertEquals("SuccessfulResult", result);
    }

    @Test
    public void checked_exception_should_be_wrapped_in_runtime() {
        try {
            unchecked(this::operationThatThrows)
                .apply("Throws");
        } catch (RuntimeException e) {
            assertTrue(e.getCause() instanceof ACheckedException);
        }
    }

    public String operationThatThrows(String input) throws ACheckedException {
        if (Objects.equals("DoNotThrow", input)) {
            return "SuccessfulResult";
        }
        throw new ACheckedException();
    }

    public static class ACheckedException extends Exception {}
}