package com.benjiweber.exceptions.handlingstrategies.either;

import com.benjiweber.exceptions.functions.ExceptionalFunction;
import com.benjiweber.exceptions.handlingstrategies.either.Either.None;
import com.benjiweber.exceptions.handlingstrategies.either.Either.Some;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.benjiweber.exceptions.handlingstrategies.either.Either.None.none;
import static com.benjiweber.exceptions.handlingstrategies.either.Either.Some.some;

public class Eitherise {

    public static <T, R, E extends Exception> Function<T,Either<R,E>> result(ExceptionalFunction<T,R,E> originalFunction) {
        return input -> {
            try {
                return some(originalFunction.apply(input));
            } catch (RuntimeException | Error e) {
                throw e;
            } catch (Exception e) {
                return none((E)e);
            }
        };
    }

    public static <T, E extends Exception, R> Function<Either<T,E>,Either<R,E>> success(Function<T,R> originalFunction) {
        return result -> {
            if (result instanceof Some) {
                T input = ((Some<T, E>) result).result();
                return some(originalFunction.apply(input));
            }
            return none(((None<T,E>)result).error());
        };
    }


    public static <T, E extends Exception> Consumer<Either<T,E>> success(Consumer<T> originalFunction) {
        return result -> {
            if (result instanceof Some) {
                T input = ((Some<T, E>) result).result();
                originalFunction.accept(input);
            }
        };
    }


    public static <T, E extends Exception>  Function<Either<T,E>,T> allOtherFailures(Consumer<E> f) {
        return result -> {
            if (result instanceof Some) {
                return ((Some<T,E>)result).result();
            }
            None<T,E> error = (None<T,E>) result;
            f.accept(error.error());
            throw new IllegalStateException(error.error());
        };
    }


    public static <T, E extends Exception> Function<Either<T,E>,Either<T,E>> failure(Class<? extends E> errorType, Function<E,T> errorHandler) {
        return result -> {
            if (result instanceof Some) {
                return ((Some<T,E>)result);
            }
            None<T,E> error = (None<T,E>) result;
            if (error.error().getClass().isAssignableFrom(errorType)) {
                return some(errorHandler.apply(error.error()));
            }
            return error;
        };
    }





}
