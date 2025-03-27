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
        validateCryptoForAdd(crypto);
        portfolio.add(crypto);
        log.info("Crypto added: {}", crypto);
    }

    @Override
    public List<Crypto> getAllCryptos(String sortByRaw) {
        Comparator<Crypto> comparator = sortingMap.get(SortBy.fromString(sortByRaw));
        log.debug("Getting all cryptos, sort: {}, result size: {}", sortByRaw, portfolio.size());

        return (comparator != null)
                ? portfolio.stream().filter(Objects::nonNull).sorted(comparator).toList()
                : List.copyOf(portfolio);
    }

    @Override
    public Crypto getCryptoById(Integer id) {
        return findCryptoById(id)
                .orElseThrow(() -> new CryptoNotFoundException(id));
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
        return portfolio.stream()
                .filter(Objects::nonNull)
                .mapToDouble(c -> c.getPrice() * c.getQuantity())
                .sum();
    }

    public void clearPortfolio() {
        portfolio.clear();
        log.info("Portfolio cleared.");
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
