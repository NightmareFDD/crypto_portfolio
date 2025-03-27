package com.engeto.crypto_portfolio.controller;

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

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CryptoController.class)
class CryptoControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("unused")
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
    void addCrypto_shouldFailValidation_onBlankName() throws Exception {
        Crypto invalidCrypto = new Crypto(1, "", "BTC", 50000, 0.5);

        mockMvc.perform(post("/crypto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCrypto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Cryptocurrency name is required")));
    }

    @Test
    void addCrypto_shouldFailValidation_onNegativePrice() throws Exception {
        Crypto invalidCrypto = new Crypto(2, "Ethereum", "ETH", -100, 2);

        mockMvc.perform(post("/crypto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCrypto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Price must be zero or positive")));
    }
}
