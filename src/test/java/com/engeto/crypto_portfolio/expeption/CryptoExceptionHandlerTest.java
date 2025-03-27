
package com.engeto.crypto_portfolio.expeption;

import com.engeto.crypto_portfolio.model.Crypto;
import com.engeto.crypto_portfolio.service.CryptoService;
import com.engeto.crypto_portfolio.service.CryptoServiceImplementation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CryptoExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CryptoService cryptoService;

    @BeforeEach
    void setup() {
        cryptoService.clearPortfolio();
    }

    @Test
    void getById_shouldReturn404_whenNotFound() throws Exception {
        mockMvc.perform(get("/cryptos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("CryptoNotFoundException"))
                .andExpect(jsonPath("$.message").value("Crypto currency with ID 99 not found."));
    }

    @Test
    void addCrypto_shouldReturn400_whenDuplicate() throws Exception {
        Crypto crypto = new Crypto(1, "Bitcoin", "BTC", 50000, 0.1);
        cryptoService.addCrypto(crypto);

        mockMvc.perform(post("/cryptos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crypto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("DuplicateCryptoException"))
                .andExpect(jsonPath("$.message").value("Crypto currency with ID 1 already exists."));
    }

    @Test
    void getAll_shouldReturn500_whenUnexpectedError() throws Exception {
        mockMvc.perform(get("/cryptos?sort=FAIL"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.type").value("Exception"))
                .andExpect(jsonPath("$.message", containsString("An unexpected error occurred")));
    }

    @TestConfiguration
    static class SpyServiceConfig {
        @Bean
        @Primary
        public CryptoService cryptoService() {
            return new CryptoServiceImplementation() {
                @Override
                public java.util.List<com.engeto.crypto_portfolio.model.Crypto> getAllCryptos(String sortBy) {
                    if ("FAIL".equalsIgnoreCase(sortBy)) {
                        throw new RuntimeException("Simulated fail");
                    }
                    return super.getAllCryptos(sortBy);
                }
            };
        }
    }
}
