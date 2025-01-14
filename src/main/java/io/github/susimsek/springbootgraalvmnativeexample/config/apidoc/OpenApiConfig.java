package io.github.susimsek.springbootgraalvmnativeexample.config.apidoc;

import io.swagger.v3.core.util.AnnotationsUtils;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;

@Configuration(proxyBeanMethods = false)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .components(new Components())
            .info(new Info()
                .title("Spring Boot GraalVM Native Example REST API")
                .description("Spring Boot GraalVM Native Example REST API Documentation")
                .version("v1.0")
                .contact(new Contact()
                    .name("Şuayb Şimşek")
                    .url("https://github.com/susimsek")
                    .email("suaybsimsek58@gmail.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("http://springdoc.org")));
    }

    @Bean
    public OpenApiCustomizer errorResponsesCustomizer() {
        return openApi -> {
            Components components = openApi.getComponents();
            openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(operation -> {
                    addErrorToApi(operation, components);
                })
            );
        };
    }

    private void addErrorToApi(Operation operation, Components components) {
        io.swagger.v3.oas.models.media.MediaType mediaType = new io.swagger.v3.oas.models.media.MediaType()
            .schema(AnnotationsUtils.resolveSchemaFromType(ProblemDetail.class, components, null));

        // 500 Internal Server Error
        operation.getResponses().addApiResponse("500", new ApiResponse()
            .description("Internal Server Error")
            .content(new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE, mediaType)));
    }
}
