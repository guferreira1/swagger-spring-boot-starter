package br.com.boot.configs.security;

import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import lombok.Data;

@Data
public class OAuth2AuthScheme implements SecurityScheme {
    private boolean enabled;
    private String description = "Autenticação OAuth2";
    private String authUrl;
    private String tokenUrl;
    private String refreshUrl;
    private String[] scopes = new String[]{"read", "write"};

    @Override
    public Type getType() {
        return Type.OAUTH2;
    }

    @Override
    public String getName() {
        return "oauth2";
    }
} 