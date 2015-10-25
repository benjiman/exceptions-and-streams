package com.benjiweber.exceptions.handlingstrategies.either;


import java.util.function.Function;
import java.util.function.Supplier;

import static com.benjiweber.exceptions.handlingstrategies.either.Either.None.none;
import static com.benjiweber.exceptions.handlingstrategies.either.Either.Some.some;

public interface Either<T,E extends Exception> {

    interface Some<T, E extends Exception> extends Either<T,E> {
        T result();
        static <T, E extends Exception> Some<T,E> some(T value) {
            return () -> value;
        }
    }
    interface None<T, E extends Exception> extends Either<T,E> {
        E error();
        static <T,R, E extends Exception> None<T,E> none(E exception) {
            return () -> exception;
        }
    }

    static <T,E extends Exception> boolean isSuccess(Either<T, E> result) {
        return result instanceof Some;
    }
    static <T,E extends Exception> boolean isError(Either<T, E> result) {
        return result instanceof None;
    }

    default <R> Either<R,E> map(Function<T,R> mapper) {
        if (this instanceof Some) {
            T input = ((Some<T, E>) this).result();
            return some(mapper.apply(input));
        } else {
            return none(((None<T,E>)this).error());
        }
    }

    default Either<T,E> orElseIf(Class<? extends E> exception, Supplier<T> replacementValue) {
        if (this instanceof None) {
            return some(replacementValue.get());
        } else {
            return some(((Some<T,E>)this).result());
        }
    }

    default T orElseThrow(Supplier<? extends RuntimeException> e) {
        if (this instanceof None) {
            throw e.get();
        }
        return ((Some<T,E>)this).result();
    }
}