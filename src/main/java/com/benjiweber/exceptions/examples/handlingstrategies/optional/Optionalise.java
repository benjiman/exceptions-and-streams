package com.benjiweber.exceptions.examples.handlingstrategies.optional;

import com.benjiweber.exceptions.examples.functions.ExceptionalFunction;

import java.util.Optional;
import java.util.function.Function;

public class Optionalise {

    public static <T,R,E extends Exception> Function<T,Optional<R>> optionalise(ExceptionalFunction<T,R,E> originalFunction) {
        return input -> {
            try {
                return Optional.ofNullable(
                        originalFunction.apply(input)
                );
            } catch (Exception e) {
                return Optional.empty();
            }
        };
    }
}
