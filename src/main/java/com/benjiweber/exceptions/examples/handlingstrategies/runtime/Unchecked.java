package com.benjiweber.exceptions.examples.handlingstrategies.runtime;

import com.benjiweber.exceptions.examples.functions.ExceptionalFunction;

import java.util.function.Function;

public class Unchecked {

    public static <T,R,E extends Exception> Function<T,R> unchecked(ExceptionalFunction<T,R,E> originalFunction) {
        return input -> {
            try {
                return originalFunction.apply(input);
            } catch (RuntimeException | Error e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

}
