package com.engeto.crypto_portfolio.expeption;

import com.engeto.crypto_portfolio.controller.CryptoController;
import com.engeto.crypto_portfolio.exception.CryptoNotFoundException;
import com.engeto.crypto_portfolio.exception.DuplicateCryptoException;
import com.engeto.crypto_portfolio.model.Crypto;
import com.engeto.crypto_portfolio.service.CryptoServiceImplementation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CryptoController.class)
class CryptoExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CryptoServiceImplementation cryptoService;

    @TestConfiguration
    static class Config {
        @Bean
        public CryptoServiceImplementation cryptoService() {
            return org.mockito.Mockito.spy(new CryptoServiceImplementation());
        }
    }

    @BeforeEach
    void resetPortfolio() {
        cryptoService.clearPortfolio();
    }

    @Test
    void addCrypto_shouldReturn400_whenDuplicateId() throws Exception {
        Crypto duplicate = new Crypto(1, "Bitcoin", "BTC", 50000, 1);
        doThrow(new DuplicateCryptoException(1)).when(cryptoService).addCrypto(duplicate);

        mockMvc.perform(post("/crypto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("DuplicateCryptoException"));
    }

    @Test
    void getById_shouldReturn404_whenNotFound() throws Exception {
        doThrow(new CryptoNotFoundException(99)).when(cryptoService).getCryptoById(99);

        mockMvc.perform(get("/crypto/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("CryptoNotFoundException"));
    }

    @Test
    void addCrypto_shouldReturn400_whenValidationFails() throws Exception {
        Crypto invalid = new Crypto(1, "", "BTC", -10, -1);

        mockMvc.perform(post("/crypto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("ValidationException"));
    }

    @Test
    void getPortfolioValue_shouldReturnCorrectValue() throws Exception {
        Crypto btc = new Crypto(1, "Bitcoin", "BTC", 60000, 0.5);
        Crypto eth = new Crypto(2, "Ethereum", "ETH", 2000, 2);
        cryptoService.addCrypto(btc);
        cryptoService.addCrypto(eth);

        mockMvc.perform(get("/crypto/portfolio-value"))
                .andExpect(status().isOk())
                .andExpect(content().string("34000.0"));
    }

    @Test
    void updateCrypto_shouldReturn204_whenSuccessful() throws Exception {
        Crypto btc = new Crypto(3, "Bitcoin", "BTC", 60000, 0.5);
        cryptoService.addCrypto(btc);

        Crypto updated = new Crypto(3, "Bitcoin", "BTC", 65000, 0.6);

        mockMvc.perform(put("/crypto/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/crypto/portfolio-value"))
                .andExpect(status().isOk())
                .andExpect(content().string("39000.0"));
    }
}