package com.engeto.crypto_portfolio.exception;

public class CryptoNotFoundException extends RuntimeException {
    public CryptoNotFoundException(Integer id) {
        super("Crypto currency with ID " + id + " not found.");
    }
}
