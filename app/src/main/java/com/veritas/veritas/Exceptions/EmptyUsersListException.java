package com.veritas.veritas.Exceptions;

public class EmptyUsersListException extends RuntimeException {
    public EmptyUsersListException(String tag) {
        super(tag);
    }
}
