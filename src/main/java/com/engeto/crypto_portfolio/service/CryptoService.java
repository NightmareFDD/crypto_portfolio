package com.engeto.crypto_portfolio.service;

import com.engeto.crypto_portfolio.model.Crypto;

import java.util.List;

public interface CryptoService {
    void addCrypto(Crypto crypto);

    List<Crypto> getAllCryptos(String sortBy);

    Crypto getCryptoById(Integer id);

    void updateCrypto(Integer id, Crypto updated);

    double getPortfolioValue();

    void clearPortfolio();
}
