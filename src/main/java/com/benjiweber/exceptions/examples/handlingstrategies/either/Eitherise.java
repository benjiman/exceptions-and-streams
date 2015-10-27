package com.benjiweber.exceptions.examples.handlingstrategies.either;

import com.benjiweber.exceptions.examples.functions.ExceptionalFunction;
import com.benjiweber.exceptions.examples.handlingstrategies.either.Either.Failure;
import com.benjiweber.exceptions.examples.handlingstrategies.either.Either.Success;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.benjiweber.exceptions.examples.handlingstrategies.either.Either.Success.success;

public class Eitherise {

    public static <T, R, E extends Exception>
        Function<T,Either<R,E>> exceptional(ExceptionalFunction<T,R,E> originalFunction) {
            return input -> {
                try {
                    return success(originalFunction.apply(input));
                } catch (RuntimeException | Error e) {
                    throw e;
                } catch (Exception e) {
                    return Failure.failure((E)e);
                }
            };
    }

    public static <T, E extends Exception, R>
        Function<Either<T,E>, Either<R,E>> onSuccess(Function<T,R> originalFunction) {
            return input -> {
                if (input instanceof Success) {
                    Success<T,E> success = (Success<T, E>) input;
                    return success(originalFunction.apply(success.result()));
                }
                return Failure.failure(((Failure<T,E>)input).reason());
            };
    }


    public static <T, E extends Exception>
        Consumer<Either<T,E>> onSuccess(Consumer<T> originalFunction) {
            return input -> {
                if (input instanceof Success) {
                    Success<T,E> success = (Success<T, E>) input;
                    originalFunction.accept(success.result());
                }
            };
    }


    public static <T, E extends Exception>
        Function<Either<T,E>,T> allOtherFailures(Consumer<E> errorHandler) {
            return result -> {
                if (result instanceof Success) {
                    Success<T, E> success = (Success<T, E>) result;
                    return success.result();
                }
                Failure<T,E> failure = (Failure<T,E>) result;
                errorHandler.accept(failure.reason());

                throw new IllegalStateException(failure.reason());
            };
    }


    public static <T, E extends Exception>
        Function<Either<T,E>, Either<T,E>> onFailure(
            Class<? extends E> errorType,
            Function<E,T> errorHandler) {
                return result -> {
                    if (result instanceof Success) {
                        return result;
                    }
                    Failure<T,E> failure = (Failure<T,E>) result;
                    if (errorType.isAssignableFrom(failure.reason().getClass())) {
                        return success(errorHandler.apply(failure.reason()));
                    }
                    return failure;
                };
    }





}
