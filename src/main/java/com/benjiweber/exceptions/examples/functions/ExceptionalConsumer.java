package com.benjiweber.exceptions.examples.functions;

public interface ExceptionalConsumer<T,E extends Exception> {
    void accept(T value) throws E;
}
