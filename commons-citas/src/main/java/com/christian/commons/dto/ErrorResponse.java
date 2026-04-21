package com.christian.commons.dto;

public record ErrorResponse(
        int codigo,
        String mensaje
) {}