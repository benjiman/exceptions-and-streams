package com.benjiweber.exceptions.examples.handlingstrategies.either;

import com.benjiweber.exceptions.examples.functions.ExceptionalConsumer;
import com.benjiweber.exceptions.examples.handlingstrategies.either.Either.Failure;
import org.junit.Test;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.benjiweber.exceptions.examples.handlingstrategies.either.Eitherise.*;
import static org.junit.Assert.*;


public class EitheriseTest {

    @Test
    public void should_convert_return_value_to_either_success() {
        Either<String, ACheckedException> result = exceptional(this::operationThatThrows).apply("DoNotThrow");
        assertEquals("SuccessfulResult", result.orElseThrow(IllegalStateException::new));
    }

    @Test
    public void should_convert_exception_to_either_failure() {
        Either<String, ACheckedException> result = exceptional(this::operationThatThrows).apply("Throw");
        assertTrue(result.isFailure());
        Failure<String, ACheckedException> failure = (Failure<String, ACheckedException>) result;
        assertTrue(failure.reason() instanceof ACheckedException);
    }

    @Test
    public void onSuccess_should_ignore_error() {
        Either<String, ACheckedException> result = exceptional(this::operationThatThrows).apply("Throw");
        Function<Either<String, ACheckedException>, Either<String, ACheckedException>> f =
                onSuccess((String s) -> { throw new RuntimeException("Should not have been invoked"); });
        Either<String, ACheckedException> finalResult = f.apply(result);
        assertTrue(finalResult.isFailure());
    }

    @Test
    public void onSuccess_should_transform_success() {
        Either<String, ACheckedException> result = exceptional(this::operationThatThrows).apply("DoNotThrow");
        Function<Either<String, ACheckedException>, Either<String, ACheckedException>> f =
                onSuccess((String s) -> s.toLowerCase());
        Either<String, ACheckedException> finalResult = f.apply(result);
        assertEquals("successfulresult", finalResult.orElseThrow(IllegalStateException::new));
    }

    @Test
    public void onSuccessTry_should_ignore_error() {
        Either<String, ACheckedException> result = exceptional(this::operationThatThrows).apply("Throw");
        Function<Either<String, ACheckedException>, Either<String, Exception>> f =
                onSuccessTry((String s) -> { throw new CheckedAssertionException("Should not have been invoked"); });
        Either<String, Exception> finalResult = f.apply(result);
        assertTrue(finalResult.isFailure());
        assertTrue(((Failure<String,Exception>)finalResult).reason() instanceof ACheckedException);
    }

    @Test
    public void onSuccessTry_should_transform_success() {
        Either<String, ACheckedException> result = exceptional(this::operationThatThrows).apply("DoNotThrow");
        Function<Either<String, ACheckedException>, Either<String, Exception>> f =
            onSuccessTry((String s) -> {
                if (false) throw new CheckedAssertionException("Should not be thrown");
                return s.toLowerCase();
            });
        Either<String, Exception> finalResult = f.apply(result);
        assertEquals("successfulresult", finalResult.orElseThrow(IllegalStateException::new));
    }

    @Test
    public void onSuccess_consumer_should_ignore_error() {
        Either<String, ACheckedException> result = exceptional(this::operationThatThrows).apply("Throw");
        Consumer<Either<String, ACheckedException>> f =
                onSuccess((Consumer<String>)s -> { throw new RuntimeException("Should not have been invoked"); });
        f.accept(result);
    }

    @Test(expected=ConsumerCalled.class)
    public void onSuccess_consumer_should_accept_success() {
        Either<String, ACheckedException> result = exceptional(this::operationThatThrows).apply("DoNotThrow");
        Consumer<Either<String, ACheckedException>> f =
                onSuccess((Consumer<String>)s -> { throw new ConsumerCalled(); });
        f.accept(result);
    }

    @Test
    public void onSuccessTry_consumer_should_ignore_error() {
        Either<String, Exception> result = Eitherise.<String, String, Exception>exceptional(this::operationThatThrows).apply("Throw");
        Consumer<Either<String, Exception>> f =
                Eitherise.<String,Exception,CheckedAssertionException>onSuccessTry((ExceptionalConsumer<String, CheckedAssertionException>) s -> { throw new CheckedAssertionException("Should not have been invoked"); });
        f.accept(result);
    }

    @Test(expected=ConsumerCalled.class)
    public void onSuccessTry_consumer_should_accept_success() {
        Either<String, Exception> result = Eitherise.<String, String, Exception>exceptional(this::operationThatThrows).apply("DoNotThrow");
        Consumer<Either<String, Exception>> f =
                Eitherise.<String,Exception,CheckedConsumerCalled>onSuccessTry(
                        (ExceptionalConsumer<String, CheckedConsumerCalled>)
                        s -> {
                            if (false) throw new CheckedConsumerCalled();
                            throw new ConsumerCalled();
                        }
                );
        f.accept(result);
    }

    @Test
    public void onFailure_should_ignore_success() {
        Either<String, ACheckedException> result = exceptional(this::operationThatThrows).apply("DoNotThrow");
        Function<Either<String, ACheckedException>, Either<String, ACheckedException>> errorHandler =
            onFailure(ACheckedException.class, error -> {
                throw new IllegalStateException("Should not have been called");
            });

        Either<String, ACheckedException> finalResult = errorHandler.apply(result);
        assertTrue(finalResult.isSuccess());
    }

    @Test
    public void onFailure_should_ignore_other_exception_types() {
        Either<String, ACheckedException> result = exceptional(this::operationThatThrows).apply("Throw");
        Function<Either<String, ACheckedException>, Either<String, ACheckedException>> errorHandler =
            onFailure(NotThrownException.class, error -> {
                throw new IllegalStateException("Should not have been called");
            });

        Either<String, ACheckedException> finalResult = errorHandler.apply(result);
        assertTrue(finalResult.isFailure());
    }


    @Test
    public void onFailure_should_use_error_handler_for_specified_exception_type() {
        Either<String, ACheckedException> result = exceptional(this::operationThatThrows).apply("Throw");
        Function<Either<String, ACheckedException>, Either<String, ACheckedException>> errorHandler =
            onFailure(ACheckedException.class, error -> "Recovered Successfully");

        Either<String, ACheckedException> finalResult = errorHandler.apply(result);
        assertEquals("Recovered Successfully", finalResult.orElseThrow(IllegalStateException::new));
    }

    @Test
    public void onFailure_should_use_error_handler_for_subtype_of_exception_type() {
        Either<String, ACheckedException> result = exceptional(this::operationThatThrows).apply("ThrowSubtype");
        Function<Either<String, ACheckedException>, Either<String, ACheckedException>> errorHandler =
                onFailure(SubtypeOfACheckedException.class, error -> "Recovered Successfully");

        Either<String, ACheckedException> finalResult = errorHandler.apply(result);
        assertEquals("Recovered Successfully", finalResult.orElseThrow(IllegalStateException::new));
    }

    @Test
    public void allOtherFailures_should_ignore_success() {
        Either<String, ACheckedException> result = exceptional(this::operationThatThrows).apply("DoNotThrow");
        Function<Either<String, ACheckedException>, String> catchAllHandler = allOtherFailures(e -> {
            throw new ConsumerCalled();
        });

        catchAllHandler.apply(result);
    }

    @Test(expected=ConsumerCalled.class)
    public void allOtherFailures_should_call_handler_on_failure() {
        Either<String, ACheckedException> result = exceptional(this::operationThatThrows).apply("Throw");
        Function<Either<String, ACheckedException>, String> catchAllHandler = allOtherFailures(e -> {
            throw new ConsumerCalled();
        });

        catchAllHandler.apply(result);
    }


    static class ConsumerCalled extends RuntimeException {}
    static class CheckedConsumerCalled extends Exception {}

    public String operationThatThrows(String input) throws ACheckedException {
        if (Objects.equals("DoNotThrow", input)) {
            return "SuccessfulResult";
        } else if (Objects.equals("ThrowSubtype", input)) {
            throw new SubtypeOfACheckedException();
        }
        throw new ACheckedException();
    }

    public static class ACheckedException extends Exception {}
    public static class NotThrownException extends ACheckedException {}
    public static class SubtypeOfACheckedException extends ACheckedException {}

    public static class CheckedAssertionException extends Exception {
        public CheckedAssertionException(String message) {
            super(message);
        }
    }

}