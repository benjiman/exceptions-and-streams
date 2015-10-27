package com.benjiweber.exceptions.examples.handlingstrategies.either;


import java.util.function.Function;
import java.util.function.Supplier;

import static com.benjiweber.exceptions.examples.handlingstrategies.either.Either.Failure.failure;
import static com.benjiweber.exceptions.examples.handlingstrategies.either.Either.Success.success;

public interface Either<T,E extends Exception> {

    interface Success<T, E extends Exception> extends Either<T,E> {
        T result();
        static <T, E extends Exception> Success<T,E> success(T value) {
            return () -> value;
        }
    }

    interface Failure<T, E extends Exception> extends Either<T,E> {
        E reason();
        static <T, E extends Exception> Failure<T,E> failure(E exception) {
            return () -> exception;
        }
    }

    default boolean isSuccess() {
        return this instanceof Success;
    }
    default boolean isFailure() {
        return this instanceof Failure;
    }

    default <R> Either<R,E> map(Function<T,R> mapper) {
        if (this instanceof Success) {
            T input = ((Success<T, E>) this).result();
            return success(mapper.apply(input));
        } else {
            return failure(((Failure<T,E>)this).reason());
        }
    }

    default Either<T,E> orElseIf(Class<? extends E> exception, Supplier<T> replacementValue) {
        if (this instanceof Failure) {
            return success(replacementValue.get());
        } else {
            return success(((Success<T,E>)this).result());
        }
    }

    default T orElseThrow(Supplier<? extends RuntimeException> e) {
        if (this instanceof Failure) {
            throw e.get();
        }
        return ((Success<T,E>)this).result();
    }
}