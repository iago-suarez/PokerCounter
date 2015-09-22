package com.poker.iago.pokercounter.exception;

public class InvalidStateError extends Error {

    public InvalidStateError(String detailMessage) {
        super(detailMessage);
    }
}
