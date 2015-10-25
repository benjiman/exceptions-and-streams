package com.benjiweber.exceptions;

import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

import static com.benjiweber.exceptions.handlingstrategies.runtime.Unchecked.unchecked;
import static java.util.stream.Collectors.toList;

public class ExampleTransformingCheckedExceptionsToRuntime {

    @Test
    public void unchecked_example() {
        List<Integer> ages =
            Stream.of("Bob","Bill")
                .map(unchecked(this::findCustomerByName))
                .peek(this::sendEmailUpdateTo)
                .map(Customer::age)
                .collect(toList());
    }

    static class UnexpectedException extends RuntimeException {}
    static class CustomerNotFound extends Exception {}
    static class CustomerUnsubscribed extends CustomerNotFound {}
    static class NoCustomerWithThatName extends CustomerNotFound {}

    public Customer findCustomerByName(String name) throws CustomerNotFound {
        return customer;
    }

    private void sendEmailUpdateTo(Customer potentialCustomer) {
        email(customer.emailAddress(), customer.name(),  "Blah blah blah");
    }

    private void email(String email, String name, String message) {

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
