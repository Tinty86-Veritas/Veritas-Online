package com.veritas.veritas.Exceptions;

public class EmptyUsersList extends RuntimeException {
    public EmptyUsersList(String tag) {
        super(tag);
    }
}
