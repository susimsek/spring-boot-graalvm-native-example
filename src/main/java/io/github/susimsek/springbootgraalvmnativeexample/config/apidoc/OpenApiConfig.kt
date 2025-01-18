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

/**
 * Configuration class for customizing the OpenAPI documentation.
 *
 * <p>This class configures the OpenAPI documentation for the application,
 * including general API information and custom error responses.</p>
 */
@Configuration(proxyBeanMethods = false)
public class OpenApiConfig {

    /**
     * Creates a custom {@link OpenAPI} bean with API metadata.
     *
     * <p>This method defines the title, description, version, contact information,
     * and license details for the API documentation.</p>
     *
     * @return a customized {@link OpenAPI} object with metadata.
     */
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

    /**
     * Customizes the OpenAPI documentation to include standardized error responses.
     *
     * <p>This method adds error response definitions (e.g., 500 Internal Server Error)
     * to all operations in the OpenAPI documentation.</p>
     *
     * @return an {@link OpenApiCustomizer} that applies error response customization.
     */
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

    /**
     * Adds a standardized error response to an API operation.
     *
     * <p>This method defines a 500 Internal Server Error response with a schema based on
     * {@link ProblemDetail} and adds it to the given operation.</p>
     *
     * @param operation  the {@link Operation} to which the error response is added.
     * @param components the {@link Components} used to resolve schemas.
     */
    private void addErrorToApi(Operation operation, Components components) {
        io.swagger.v3.oas.models.media.MediaType mediaType = new io.swagger.v3.oas.models.media.MediaType()
            .schema(AnnotationsUtils.resolveSchemaFromType(ProblemDetail.class, components, null));

        // 500 Internal Server Error
        operation.getResponses().addApiResponse("500", new ApiResponse()
            .description("Internal Server Error")
            .content(new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE, mediaType)));
    }
}
