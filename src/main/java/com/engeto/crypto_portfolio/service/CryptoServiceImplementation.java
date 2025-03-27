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
            SortBy.NAME, Comparator.comparing(Crypto::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)),
            SortBy.PRICE, Comparator.comparingDouble(Crypto::getPrice),
            SortBy.QUANTITY, Comparator.comparingDouble(Crypto::getQuantity)
    );


    @Override
    public void addCrypto(Crypto crypto) {
        validateCryptoForAdd(crypto);
        portfolio.add(crypto);
        log.info("Crypto added: {}", crypto);
    }

    @Override
    public List<Crypto> getAllCryptos(String sortByRaw) {
        if (sortByRaw != null && !sortByRaw.isBlank()) {
            log.debug("Getting sorted portfolio by: {}", sortByRaw);
            Comparator<Crypto> comparator = resolveComparator(sortByRaw);
            return getSortedPortfolio(comparator);
        }

        log.debug("Getting unsorted portfolio.");
        return getUnsortedPortfolio();
    }

    @Override
    public Crypto getCryptoById(Integer id) {
        log.debug("Looking for crypto with ID: {}", id);
        return findCryptoById(id).orElseThrow(() -> new CryptoNotFoundException(id));
    }

    @Override
    public void updateCrypto(Integer id, Crypto updated) {
        validateCryptoForUpdate(updated);
        int index = findCryptoIndexByIdOrThrow(id);
        portfolio.set(index, updated);
        log.info("Crypto updated: {}", updated);
    }

    @Override
    public double getPortfolioValue() {
        double total = portfolio.stream()
                .filter(Objects::nonNull)
                .mapToDouble(c -> c.getPrice() * c.getQuantity())
                .sum();
        log.debug("Calculated portfolio value: {}", total);
        return total;
    }

    @Override
    public void clearPortfolio() {
        portfolio.clear();
    }

    private List<Crypto> getSortedPortfolio(Comparator<Crypto> comparator) {
        return portfolio.stream()
                .filter(Objects::nonNull)
                .sorted(comparator)
                .toList();
    }

    private List<Crypto> getUnsortedPortfolio() {
        return portfolio.stream()
                .filter(Objects::nonNull)
                .toList();
    }

    private Comparator<Crypto> resolveComparator(String sortByRaw) {
        SortBy sortBy = SortBy.fromString(sortByRaw);
        return sortingMap.getOrDefault(sortBy, Comparator.comparing(Crypto::getId));
    }

    private Optional<Crypto> findCryptoById(Integer id) {
        return portfolio.stream()
                .filter(c -> c != null && id.equals(c.getId()))
                .findFirst();
    }

    private int findCryptoIndexByIdOrThrow(Integer id) {
        return IntStream.range(0, portfolio.size())
                .filter(i -> portfolio.get(i) != null && id.equals(portfolio.get(i).getId()))
                .findFirst()
                .orElseThrow(() -> new CryptoNotFoundException(id));
    }

    private void assertIdIsUnique(Integer id) {
        if (findCryptoById(id).isPresent()) {
            throw new DuplicateCryptoException(id);
        }
    }

    private void validateCryptoForAdd(Crypto crypto) {
        Objects.requireNonNull(crypto, "Crypto must not be null");
        Objects.requireNonNull(crypto.getId(), "Crypto ID must not be null");
        log.debug("Checking for duplicate ID before adding: {}", crypto.getId());
        assertIdIsUnique(crypto.getId());
    }

    private void validateCryptoForUpdate(Crypto crypto) {
        Objects.requireNonNull(crypto, "Updated crypto must not be null");
        Objects.requireNonNull(crypto.getId(), "Updated crypto ID must not be null");
    }
}