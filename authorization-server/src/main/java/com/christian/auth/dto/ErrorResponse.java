package com.christian.auth.dto;

public record ErrorResponse(
        int codigo,
        String mensaje
) { }
