package br.com.boot.configs;

import br.com.boot.annotations.Api;
import br.com.boot.configs.security.*;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.method.HandlerMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SwaggerProperties.class)
public class OpenApiAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public OpenAPI customOpenAPI(final SwaggerProperties properties) {
        OpenAPI openApi = new OpenAPI()
                .info(buildInfo(properties))
                .servers(buildServers(properties));

        configureSecuritySchemes(openApi, properties);
        configureExternalDocs(openApi, properties);
        configureTags(openApi, properties);

        return openApi;
    }

    @Bean
    @ConditionalOnMissingBean
    public GroupedOpenApi customApi(final SwaggerProperties properties,
                                    final OperationCustomizer apiCustomizer) {
        GroupedOpenApi.Builder builder = GroupedOpenApi.builder()
                .group("api")
                .addOperationCustomizer(apiCustomizer);

        if (properties.getPathsToMatch() != null && !properties.getPathsToMatch().isEmpty()) {
            builder.pathsToMatch(properties.getPathsToMatch().toArray(new String[0]));
        }
        if (properties.getPathsToExclude() != null && !properties.getPathsToExclude().isEmpty()) {
            builder.pathsToExclude(properties.getPathsToExclude().toArray(new String[0]));
        }
        if ((properties.getPathsToMatch() == null || properties.getPathsToMatch().isEmpty())
                && (properties.getPathsToExclude() == null || properties.getPathsToExclude().isEmpty())) {
            builder.pathsToMatch("/**");
        }

        return builder.build();
    }


    @Bean
    public OperationCustomizer apiCustomizer() {
        return (operation, handler) -> {
            Api ann = AnnotationUtils.findAnnotation(handler.getMethod(), Api.class);
            if (Objects.isNull(ann)) {
                return operation;
            }

            operation.setSummary(ann.summary());
            operation.getResponses().clear();

            if (!Void.class.equals(ann.success())) {
                int code = detectSuccessCode(handler);
                operation.getResponses()
                        .addApiResponse(String.valueOf(code), buildResponse(code, ann.success()));
            }

            for (String entry : ann.errors()) {
                String[] parts = entry.split(":", 2);
                String statusCode = parts[0].trim();
                String desc = parts.length > 1
                        ? parts[1].trim()
                        : Optional.ofNullable(HttpStatus.resolve(Integer.parseInt(statusCode)))
                        .map(HttpStatus::getReasonPhrase)
                        .orElse("Error " + statusCode);
                operation.getResponses()
                        .addApiResponse(statusCode, new ApiResponse().description(desc));
            }
            return operation;
        };
    }

    private int detectSuccessCode(final HandlerMethod handler) {
        if (handler.hasMethodAnnotation(PostMapping.class))   return 201;
        if (handler.hasMethodAnnotation(DeleteMapping.class)) return 204;
        return 200;
    }

    private ApiResponse buildResponse(final int code, final Class<?> model) {
        String desc = Optional.ofNullable(HttpStatus.resolve(code))
                .map(HttpStatus::getReasonPhrase)
                .orElse("Status " + code);

        ApiResponse resp = new ApiResponse().description(desc);

        if (code >= 200 && code < 600 && !Void.class.equals(model)) {
            String ref = "#/components/schemas/" + model.getSimpleName();
            Schema<?> schema = new Schema<>().$ref(ref);
            Content content = new Content()
                    .addMediaType("application/json", new MediaType().schema(schema));
            resp.setContent(content);
        }

        return resp;
    }

    private Info buildInfo(final SwaggerProperties props) {
        return new Info()
                .title(props.getTitle())
                .description(props.getDescription())
                .version(props.getVersion())
                .contact(new Contact()
                        .name(props.getContact().getName())
                        .email(props.getContact().getEmail())
                        .url(props.getContact().getUrl()))
                .license(new License()
                        .name(props.getLicense().getName())
                        .url(props.getLicense().getUrl()));
    }

    private List<Server> buildServers(final SwaggerProperties props) {
        List<Server> list = new ArrayList<>();
        for (SwaggerProperties.Server s : props.getServers()) {
            list.add(new Server().url(s.getUrl()).description(s.getDescription()));
        }
        return list;
    }

    private void configureSecuritySchemes(OpenAPI openApi, SwaggerProperties props) {
        Components comps = new Components();
        List<SecurityRequirement> reqs = new ArrayList<>();

        if (props.getSecurity().getBasic().isEnabled()) {
            configureScheme(comps, reqs, props.getSecurity().getBasic());
        }
        if (props.getSecurity().getBearer().isEnabled()) {
            configureScheme(comps, reqs, props.getSecurity().getBearer());
        }
        if (props.getSecurity().getApiKey().isEnabled()) {
            configureScheme(comps, reqs, props.getSecurity().getApiKey());
        }
        if (props.getSecurity().getOauth2().isEnabled()) {
            configureScheme(comps, reqs, props.getSecurity().getOauth2());
        }

        if (comps.getSecuritySchemes() != null && !comps.getSecuritySchemes().isEmpty()) {
            openApi.setComponents(comps);
            openApi.setSecurity(reqs);
        }
    }

    private void configureScheme(final Components comps,
                                 final List<SecurityRequirement> reqs,
                                 final SecurityScheme scheme) {
        io.swagger.v3.oas.models.security.SecurityScheme s =
                new io.swagger.v3.oas.models.security.SecurityScheme()
                        .type(scheme.getType())
                        .description(scheme.getName());

        if (scheme instanceof BearerAuthScheme b) {
            s.scheme("bearer").bearerFormat(b.getBearerFormat());
        } else if (scheme instanceof BasicAuthScheme) {
            s.scheme("basic");
        } else if (scheme instanceof ApiKeyAuthScheme a) {
            s.in(a.getIn()).name(a.getKeyName());
        } else if (scheme instanceof OAuth2AuthScheme o) {
            s.flows(new OAuthFlows()
                    .authorizationCode(new OAuthFlow()
                            .authorizationUrl(o.getAuthUrl())
                            .tokenUrl(o.getTokenUrl())
                            .refreshUrl(o.getRefreshUrl())));
        }

        comps.addSecuritySchemes(scheme.getName(), s);
        reqs.add(new SecurityRequirement().addList(scheme.getName()));
    }

    private void configureExternalDocs(OpenAPI openApi, SwaggerProperties props) {
        if (StringUtils.hasText(props.getExternalDocs().getUrl())) {
            openApi.setExternalDocs(new ExternalDocumentation()
                    .description(props.getExternalDocs().getDescription())
                    .url(props.getExternalDocs().getUrl()));
        }
    }

    private void configureTags(OpenAPI openApi, SwaggerProperties props) {
        List<Tag> tags = new ArrayList<>();
        for (SwaggerProperties.Tag t : props.getTags()) {
            tags.add(new Tag().name(t.getName()).description(t.getDescription()));
        }
        if (!tags.isEmpty()) {
            openApi.setTags(tags);
        }
    }
}
