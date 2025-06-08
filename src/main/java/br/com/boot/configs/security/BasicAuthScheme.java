package br.com.boot.configs.security;

import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import lombok.Data;

@Data
public class BasicAuthScheme implements SecurityScheme {
    private boolean enabled;
    private String description = "Autenticação Básica";

    @Override
    public Type getType() {
        return Type.HTTP;
    }

    @Override
    public String getName() {
        return "basicAuth";
    }
} 