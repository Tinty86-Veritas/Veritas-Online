package com.veritas.veritas.Exceptions;

public class NotEnoughPlayers extends RuntimeException {
    public NotEnoughPlayers(String tag) {
        super(tag);
    }
}
