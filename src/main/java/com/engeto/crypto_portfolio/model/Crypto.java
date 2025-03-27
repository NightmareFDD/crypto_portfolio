package com.engeto.crypto_portfolio.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Crypto {

    @NotNull(message = "ID must not be null")
    private Integer id;

    @NotBlank(message = "Cryptocurrency name is required")
    private String name;

    @NotBlank(message = "Cryptocurrency symbol is required")
    private String symbol;

    @PositiveOrZero(message = "Price must be zero or positive")
    private double price;

    @PositiveOrZero(message = "Quantity must be zero or positive")
    private double quantity;

}
