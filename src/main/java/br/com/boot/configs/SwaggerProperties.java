package br.com.boot.configs;

import br.com.boot.configs.security.*;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "swagger")
public class SwaggerProperties {
    private String group = "default";
    private String title = "API Documentation";
    private String description;
    private String version = "1.0.0";
    private String basePackage;
    private List<Server> servers = new ArrayList<>();
    private List<String> pathsToMatch = new ArrayList<>();
    private List<String> pathsToExclude = new ArrayList<>();
    
    @NestedConfigurationProperty
    private SecurityConfig security = new SecurityConfig();
    
    @NestedConfigurationProperty
    private Contact contact = new Contact();
    
    @NestedConfigurationProperty
    private License license = new License();
    
    @NestedConfigurationProperty
    private ExternalDocs externalDocs = new ExternalDocs();
    
    private List<Tag> tags = new ArrayList<>();
    
    @NestedConfigurationProperty
    private Cache cache = new Cache();
    
    @NestedConfigurationProperty
    private Cors cors = new Cors();
    
    @NestedConfigurationProperty
    private RateLimit rateLimit = new RateLimit();

    @Data
    public static class Server {
        private String url;
        private String description;
    }

    @Data
    public static class SecurityConfig {
        @NestedConfigurationProperty
        private BasicAuthScheme basic = new BasicAuthScheme();
        
        @NestedConfigurationProperty
        private BearerAuthScheme bearer = new BearerAuthScheme();
        
        @NestedConfigurationProperty
        private ApiKeyAuthScheme apiKey = new ApiKeyAuthScheme();
        
        @NestedConfigurationProperty
        private OAuth2AuthScheme oauth2 = new OAuth2AuthScheme();
    }

    @Data
    public static class Contact {
        private String name;
        private String email;
        private String url;
    }

    @Data
    public static class License {
        private String name;
        private String url;
    }

    @Data
    public static class ExternalDocs {
        private String description;
        private String url;
    }

    @Data
    public static class Tag {
        private String name;
        private String description;
    }

    @Data
    public static class Cache {
        private boolean enabled = false;
        private long timeout = 3600;
    }

    @Data
    public static class Cors {
        private boolean enabled = false;
        private List<String> allowedOrigins = new ArrayList<>();
        private List<String> allowedMethods = new ArrayList<>();
        private List<String> allowedHeaders = new ArrayList<>();
    }

    @Data
    public static class RateLimit {
        private boolean enabled = false;
        private int requestsPerSecond = 10;
    }
}