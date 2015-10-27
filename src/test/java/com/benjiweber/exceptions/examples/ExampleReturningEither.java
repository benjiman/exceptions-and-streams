package com.benjiweber.exceptions.examples;

import com.benjiweber.exceptions.examples.handlingstrategies.either.Either;
import org.junit.Test;

import static com.benjiweber.exceptions.examples.handlingstrategies.either.Either.Failure.failure;

public class ExampleReturningEither {


    static class UnexpectedException extends RuntimeException {}
    static class CustomerNotFound extends Exception {}
    static class CustomerUnsubscribed extends CustomerNotFound {}
    static class NoCustomerWithThatName extends CustomerNotFound {}

    public Either<Customer, CustomerNotFound> findCustomerByName(String name) {
        return failure(new NoCustomerWithThatName());
    }

    @Test
    public void either_example() {
        String name = findCustomerByName("Bob")
            .map(Customer::name)
            .orElseIf(NoCustomerWithThatName.class, () -> "Unknown Customer")
            .orElseThrow(UnexpectedException::new);
    }


    interface Customer {
        default void sendEmail(String s) {}
        default void updateLastSpammedDate(){};

        default String emailAddress(){ return "";}
        default String name() { return ""; }
        default int age() { return 0; }
    }

    static Customer customer = new Customer(){};

}
