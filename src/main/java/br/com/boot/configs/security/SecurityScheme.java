package br.com.boot.configs.security;

import io.swagger.v3.oas.models.security.SecurityScheme.Type;

public interface SecurityScheme {
    Type getType();
    String getName();
    boolean isEnabled();
} 