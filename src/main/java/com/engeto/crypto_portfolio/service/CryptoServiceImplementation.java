package com.engeto.crypto_portfolio.service;

import com.engeto.crypto_portfolio.exception.CryptoNotFoundException;
import com.engeto.crypto_portfolio.exception.DuplicateCryptoException;
import com.engeto.crypto_portfolio.model.Crypto;
import com.engeto.crypto_portfolio.model.SortBy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@Service
public class CryptoServiceImplementation implements CryptoService {

    private final List<Crypto> portfolio = new ArrayList<>();

    private final Map<SortBy, Comparator<Crypto>> sortingMap = Map.of(
            SortBy.NAME, Comparator.comparing(Crypto::getName, String.CASE_INSENSITIVE_ORDER),
            SortBy.PRICE, Comparator.comparingDouble(Crypto::getPrice),
            SortBy.QUANTITY, Comparator.comparingDouble(Crypto::getQuantity)
    );

    @Override
    public void addCrypto(Crypto crypto) {
        log.debug("Checking for duplicate ID before adding: {}", crypto.getId());
        assertIdIsUnique(crypto.getId());
        portfolio.add(crypto);
        log.info("Crypto added: {}", crypto);
    }

    @Override
    public List<Crypto> getAllCryptos(String sortByRaw) {
        SortBy sortBy = SortBy.fromString(sortByRaw);
        Comparator<Crypto> comparator = sortingMap.get(sortBy);

        log.debug("Getting all cryptos, sort: {}, result size: {}", sortBy, portfolio.size());

        return comparator != null
                ? portfolio.stream().sorted(comparator).toList()
                : new ArrayList<>(portfolio);
    }

    @Override
    public Crypto getCryptoById(Integer id) {
        log.debug("Looking for crypto with ID: {}", id);
        return findCryptoById(id).orElseThrow(() -> new CryptoNotFoundException(id));
    }

    private Optional<Crypto> findCryptoById(Integer id) {
        return portfolio.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    @Override
    public void updateCrypto(Integer id, Crypto updated) {
        log.debug("Updating crypto with ID {}", id);
        int index = findCryptoIndexByIdOrThrow(id);
        portfolio.set(index, updated);
        log.info("Crypto updated: {}", updated);
    }

    @Override
    public double getPortfolioValue() {
        double total = portfolio.stream()
                .mapToDouble(c -> c.getPrice() * c.getQuantity())
                .sum();
        log.debug("Calculated portfolio value: {}", total);
        return total;
    }

    public void clearPortfolio() {
        portfolio.clear();
    }

    private void assertIdIsUnique(Integer id) {
        if (findCryptoById(id).isPresent()) {
            throw new DuplicateCryptoException(id);
        }
    }

    private int findCryptoIndexByIdOrThrow(Integer id) {
        return IntStream.range(0, portfolio.size())
                .filter(i -> id.equals(portfolio.get(i).getId()))
                .findFirst()
                .orElseThrow(() -> new CryptoNotFoundException(id));
    }
}