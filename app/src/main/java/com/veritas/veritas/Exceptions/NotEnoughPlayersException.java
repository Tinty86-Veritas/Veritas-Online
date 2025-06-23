package com.veritas.veritas.Exceptions;

public class NotEnoughPlayersException extends RuntimeException {
    public NotEnoughPlayersException(String tag) {
        super(tag);
    }
}
