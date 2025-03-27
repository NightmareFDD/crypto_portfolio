package com.engeto.crypto_portfolio.controller;

import com.engeto.crypto_portfolio.exception.CryptoNotFoundException;
import com.engeto.crypto_portfolio.exception.DuplicateCryptoException;
import com.engeto.crypto_portfolio.model.Crypto;
import com.engeto.crypto_portfolio.service.CryptoServiceImplementation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.mockito.Mockito;
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
    private CryptoServiceImplementation cryptoService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CryptoServiceImplementation cryptoService() {
            return Mockito.spy(new CryptoServiceImplementation());
        }
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addCrypto_shouldReturn400_whenDuplicateId() throws Exception {
        Crypto duplicate = new Crypto(1, "Bitcoin", "BTC", 50000, 1);

        doThrow(new DuplicateCryptoException(1)).when(cryptoService).addCrypto(duplicate);

        mockMvc.perform(post("/crypto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("already exists")));
    }

    @Test
    void getById_shouldReturn404_whenNotFound() throws Exception {
        doThrow(new CryptoNotFoundException(99)).when(cryptoService).getCryptoById(99);

        mockMvc.perform(get("/crypto/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("not found")));
    }
}