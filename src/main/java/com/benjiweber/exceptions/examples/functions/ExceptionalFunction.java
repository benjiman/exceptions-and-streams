package com.benjiweber.exceptions.examples.functions;

public interface ExceptionalFunction<T,R,E extends Exception> {
    R apply(T input) throws E;
}
