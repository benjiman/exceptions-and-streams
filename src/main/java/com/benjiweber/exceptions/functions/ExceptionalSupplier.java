package com.benjiweber.exceptions.functions;

public interface ExceptionalSupplier<T,R,E extends Exception> {
    R get() throws E;
}
