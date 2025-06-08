# Swagger Spring Boot Starter

Biblioteca para simplificar a integração do Swagger/OpenAPI em projetos Spring Boot, reduzindo a verbosidade e automatizando configurações comuns.

## Requisitos

- Java 17 ou superior
- Spring Boot 3.x

## Instalação

Adicione a dependência no seu `pom.xml`:

```xml
<dependency>
    <groupId>br.com.boot</groupId>
    <artifactId>swagger-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Uso Básico

A biblioteca oferece configuração automática através do arquivo `application.properties` ou `application.yml`.

### Usando application.yml:

```yaml
swagger:
  title: Nome da API
  description: Descrição da API
  version: 1.0.0
  base-package: br.com.sua.api
  servers:
    - url: http://localhost:8080
      description: Ambiente Local
```

### Usando application.properties:

```properties
swagger.title=Nome da API
swagger.description=Descrição da API
swagger.version=1.0.0
swagger.base-package=br.com.sua.api
swagger.servers[0].url=http://localhost:8080
swagger.servers[0].description=Ambiente Local
```

## Recursos Avançados

### 1. Anotação @Api

Use a anotação `@Api` para documentar seus endpoints de forma simples e clara:

```java
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @PostMapping
    @Api(
        summary = "Criar novo usuário",
        success = UsuarioResponse.class,
        errors = {
            "400: Dados inválidos",
            "409: Usuário já existe"
        }
    )
    public ResponseEntity<UsuarioResponse> criar(@RequestBody UsuarioRequest request) {
        // implementação
    }
}
```

### 2. Configurações de Segurança

#### Basic Auth

```yaml
swagger:
  security:
    basic:
      enabled: true
      description: Autenticação Básica
```

#### Bearer Token (JWT)

```yaml
swagger:
  security:
    bearer:
      enabled: true
      description: Autenticação JWT
      bearer-format: JWT
```

#### API Key

```yaml
swagger:
  security:
    api-key:
      enabled: true
      description: Chave de API
      key-name: X-API-KEY
      in: header # ou query, cookie
```

#### OAuth2

```yaml
swagger:
  security:
    oauth2:
      enabled: true
      description: Autenticação OAuth2
      auth-url: http://seu-auth-server/oauth/authorize
      token-url: http://seu-auth-server/oauth/token
      refresh-url: http://seu-auth-server/oauth/refresh
      scopes:
        - read
        - write
```

### 3. Configurações de Cache

```yaml
swagger:
  cache:
    enabled: true
    timeout: 3600 # segundos
```

### 4. Configurações de CORS

```yaml
swagger:
  cors:
    enabled: true
    allowed-origins:
      - http://localhost:3000
      - https://sua-aplicacao.com
    allowed-methods:
      - GET
      - POST
      - PUT
      - DELETE
    allowed-headers:
      - Authorization
      - Content-Type
```

### 5. Rate Limiting

```yaml
swagger:
  rate-limit:
    enabled: true
    requests-per-second: 10
```

### 6. Documentação Externa e Tags

```yaml
swagger:
  external-docs:
    description: Documentação Adicional
    url: https://docs.sua-api.com

  tags:
    - name: usuarios
      description: Operações relacionadas a usuários
    - name: produtos
      description: Operações relacionadas a produtos
```

### 7. Filtros de Path

```yaml
swagger:
  paths-to-match:
    - /api/**
    - /public/**
  paths-to-exclude:
    - /api/admin/**
    - /api/internal/**
```

### 8. Informações de Contato e Licença

```yaml
swagger:
  contact:
    name: Nome do Desenvolvedor
    email: dev@empresa.com
    url: https://empresa.com

  license:
    name: MIT
    url: https://opensource.org/licenses/MIT
```

## Detecção Automática

A biblioteca detecta automaticamente:

- Códigos HTTP apropriados (201 para POST, 204 para DELETE, etc.)
- Schemas de resposta baseados no tipo de retorno
- Paths da API (fallback para `/**` se não especificado)

## Customização

Para casos específicos, você pode sobrescrever qualquer bean:

```java
@Configuration
public class CustomSwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // sua customização
    }

    @Bean
    public GroupedOpenApi customApi() {
        // sua customização
    }
}
```

## Contribuição

Contribuições são bem-vindas! Por favor, sinta-se à vontade para submeter um Pull Request.

## Licença

Este projeto está licenciado sob a licença MIT - veja o arquivo [LICENSE](LICENSE) para mais detalhes.
