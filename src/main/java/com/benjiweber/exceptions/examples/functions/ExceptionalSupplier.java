package com.benjiweber.exceptions.examples.functions;

public interface ExceptionalSupplier<T,R,E extends Exception> {
    R get() throws E;
}
