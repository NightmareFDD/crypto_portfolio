package com.engeto.crypto_portfolio.exception;

import java.time.LocalDateTime;

public record ApiError(
        int status,
        String message,
        String type,
        String path,
        LocalDateTime timestamp
) {}