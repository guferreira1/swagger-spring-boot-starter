package br.com.boot.configs.security;

import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import lombok.Data;

@Data
public class ApiKeyAuthScheme implements SecurityScheme {
    private boolean enabled;
    private String description = "Autenticação via API Key";
    private String keyName = "X-API-KEY";
    private In in = In.HEADER;

    @Override
    public Type getType() {
        return Type.APIKEY;
    }

    @Override
    public String getName() {
        return "apiKey";
    }
} 