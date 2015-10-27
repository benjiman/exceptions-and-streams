package com.benjiweber.exceptions.handlingstrategies.either;

import com.benjiweber.exceptions.functions.ExceptionalFunction;
import com.benjiweber.exceptions.handlingstrategies.either.Either.Error;
import com.benjiweber.exceptions.handlingstrategies.either.Either.Success;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.benjiweber.exceptions.handlingstrategies.either.Either.Error.error;
import static com.benjiweber.exceptions.handlingstrategies.either.Either.Success.success;

public class Eitherise {

    public static <T, R, E extends Exception> Function<T,Either<R,E>> exceptional(ExceptionalFunction<T,R,E> originalFunction) {
        return input -> {
            try {
                return success(originalFunction.apply(input));
            } catch (RuntimeException | java.lang.Error e) {
                throw e;
            } catch (Exception e) {
                return error((E)e);
            }
        };
    }

    public static <T, E extends Exception, R> Function<Either<T,E>,Either<R,E>> onSuccess(Function<T,R> originalFunction) {
        return result -> {
            if (result instanceof Success) {
                T input = ((Success<T, E>) result).result();
                return success(originalFunction.apply(input));
            }
            return error(((Error<T,E>)result).error());
        };
    }


    public static <T, E extends Exception> Consumer<Either<T,E>> onSuccess(Consumer<T> originalFunction) {
        return result -> {
            if (result instanceof Success) {
                T input = ((Success<T, E>) result).result();
                originalFunction.accept(input);
            }
        };
    }


    public static <T, E extends Exception>  Function<Either<T,E>,T> allOtherFailures(Consumer<E> f) {
        return result -> {
            if (result instanceof Success) {
                return ((Success<T,E>)result).result();
            }
            Error<T,E> error = (Error<T,E>) result;
            f.accept(error.error());
            throw new IllegalStateException(error.error());
        };
    }


    public static <T, E extends Exception> Function<Either<T,E>,Either<T,E>> onFailure(Class<? extends E> errorType, Function<E,T> errorHandler) {
        return result -> {
            if (result instanceof Success) {
                return ((Success<T,E>)result);
            }
            Error<T,E> error = (Error<T,E>) result;
            if (error.error().getClass().isAssignableFrom(errorType)) {
                return success(errorHandler.apply(error.error()));
            }
            return error;
        };
    }





}
