package com.engeto.crypto_portfolio.controller;

import com.engeto.crypto_portfolio.model.Crypto;
import com.engeto.crypto_portfolio.service.CryptoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/cryptos")
public class CryptoController {

    private final CryptoService cryptoService;

    @PostMapping
    public ResponseEntity<Void> addCrypto(@RequestBody @Validated Crypto crypto) {
        log.info("Received request to add crypto: {}", crypto);
        cryptoService.addCrypto(crypto);
        return ResponseEntity.created(URI.create("/cryptos/" + crypto.getId())).build();
    }

    @GetMapping
    public ResponseEntity<List<Crypto>> getAll(@RequestParam(required = false, defaultValue = "") String sort) {
        log.info("Received request to get all cryptos with sort: {}", sort);
        return ResponseEntity.ok(cryptoService.getAllCryptos(sort));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Crypto> getById(@PathVariable Integer id) {
        log.info("Received request to get crypto by ID: {}", id);
        return ResponseEntity.ok(cryptoService.getCryptoById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCrypto(@PathVariable Integer id, @RequestBody @Validated Crypto crypto) {
        log.info("Received request to update crypto ID {}: {}", id, crypto);
        cryptoService.updateCrypto(id, crypto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/portfolio-value")
    public ResponseEntity<Double> getTotalValue() {
        double value = cryptoService.getPortfolioValue();
        log.info("Received request to calculate portfolio value. Result: {}", value);
        return ResponseEntity.ok(value);
    }
}
