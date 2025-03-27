package com.engeto.crypto_portfolio.service;

import com.engeto.crypto_portfolio.exception.DuplicateCryptoException;
import com.engeto.crypto_portfolio.model.Crypto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CryptoServiceTest {

    private CryptoServiceImplementation service;

    @BeforeEach
    void setUp() {
        service = new CryptoServiceImplementation();
    }

    @Test
    void addCrypto_addsSuccessfully() {
        Crypto btc = new Crypto(1, "Bitcoin", "BTC", 60000, 1);
        service.addCrypto(btc);

        List<Crypto> result = service.getAllCryptos("");
        assertEquals(1, result.size());
        assertEquals("Bitcoin", result.getFirst().getName());
    }

    @Test
    void addCrypto_throwsOnDuplicateId() {
        Crypto btc = new Crypto(1, "Bitcoin", "BTC", 60000, 1);
        service.addCrypto(btc);

        assertThrows(DuplicateCryptoException.class, () -> service.addCrypto(btc));
    }

    @Test
    void getPortfolioValue_returnsCorrectTotal() {
        service.addCrypto(new Crypto(1, "BTC", "BTC", 50000, 0.5));
        service.addCrypto(new Crypto(2, "ETH", "ETH", 2500, 2));

        double value = service.getPortfolioValue();
        assertEquals(25000 + 5000, value);
    }
}