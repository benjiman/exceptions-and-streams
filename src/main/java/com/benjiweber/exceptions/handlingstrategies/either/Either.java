package com.benjiweber.exceptions.handlingstrategies.either;


import java.util.function.Function;
import java.util.function.Supplier;

import static com.benjiweber.exceptions.handlingstrategies.either.Either.Error.error;
import static com.benjiweber.exceptions.handlingstrategies.either.Either.Success.success;

public interface Either<T,E extends Exception> {

    interface Success<T, E extends Exception> extends Either<T,E> {
        T result();
        static <T, E extends Exception> Success<T,E> success(T value) {
            return () -> value;
        }
    }
    interface Error<T, E extends Exception> extends Either<T,E> {
        E error();
        static <T,R, E extends Exception> Error<T,E> error(E exception) {
            return () -> exception;
        }
    }

    static <T,E extends Exception> boolean isSuccess(Either<T, E> result) {
        return result instanceof Success;
    }
    static <T,E extends Exception> boolean isError(Either<T, E> result) {
        return result instanceof Error;
    }

    default <R> Either<R,E> map(Function<T,R> mapper) {
        if (this instanceof Success) {
            T input = ((Success<T, E>) this).result();
            return success(mapper.apply(input));
        } else {
            return error(((Error<T,E>)this).error());
        }
    }

    default Either<T,E> orElseIf(Class<? extends E> exception, Supplier<T> replacementValue) {
        if (this instanceof Error) {
            return success(replacementValue.get());
        } else {
            return success(((Success<T,E>)this).result());
        }
    }

    default T orElseThrow(Supplier<? extends RuntimeException> e) {
        if (this instanceof Error) {
            throw e.get();
        }
        return ((Success<T,E>)this).result();
    }
}