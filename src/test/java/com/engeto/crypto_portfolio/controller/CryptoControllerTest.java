package com.engeto.crypto_portfolio.controller;

import com.engeto.crypto_portfolio.model.Crypto;
import com.engeto.crypto_portfolio.service.CryptoServiceImplementation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CryptoController.class)
class CryptoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("unused")
    @Autowired
    private CryptoServiceImplementation cryptoService;

    @TestConfiguration
    static class SpyConfig {
        @Bean
        public CryptoServiceImplementation cryptoService() {
            return Mockito.spy(new CryptoServiceImplementation());
        }
    }

    @Test
    void addCrypto_shouldReturn201() throws Exception {
        Crypto crypto = new Crypto(1, "BTC", "BTC", 50000, 0.1);

        mockMvc.perform(post("/cryptos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crypto)))
                .andExpect(status().isCreated());
    }

    @Test
    void getAll_shouldReturnEmptyListInitially() throws Exception {
        mockMvc.perform(get("/cryptos"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}