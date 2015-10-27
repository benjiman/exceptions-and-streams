package com.benjiweber.exceptions.examples;

import com.benjiweber.exceptions.examples.handlingstrategies.either.Either;
import com.benjiweber.exceptions.examples.handlingstrategies.either.Eitherise;
import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ExampleHandlingCheckedExceptionsInStreamsWithEither {

    @Test
    public void handling_exceptions_with_either_example() {
        List<Integer> customerAges =
            Stream.of("Bob", "Bill")
                .map(Eitherise.exceptional(this::findCustomerByName))
                .peek(Eitherise.onSuccess(this::sendEmailUpdateTo))
                .map(Eitherise.onSuccess(Customer::age))
                .map(Eitherise.onFailure(NoCustomerWithThatName.class, error -> {
                    log("Customer not found :(");
                    return -1;
                }))
                .map(Eitherise.allOtherFailures(ex -> {
                    throw new RuntimeException(ex);
                }))
                .collect(toList());
    }

    @Test
    public void creating_stream_of_eithers() {
        Stream<Either<Customer, CustomerNotFound>> customers =
            Stream.of("Bob", "Bill")
                .map(Eitherise.exceptional(this::findCustomerByName));
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

    private void log(String s) {

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
