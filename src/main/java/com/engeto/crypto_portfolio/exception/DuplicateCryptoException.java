package com.engeto.crypto_portfolio.exception;

public class DuplicateCryptoException extends RuntimeException {
    public DuplicateCryptoException(Integer id) {
        super("Crypto currency with ID " + id + " already exists.");
    }
}