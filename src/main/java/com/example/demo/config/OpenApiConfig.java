package com.example.demo.config;

import com.example.demo.config.properties.DemoProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
@OpenAPIDefinition
@SecurityScheme(type = SecuritySchemeType.APIKEY, name = HttpHeaders.AUTHORIZATION, in = SecuritySchemeIn.HEADER,
                bearerFormat = "bearer")
public class OpenApiConfig {

    private final DemoProperties demoProperties;

    public OpenApiConfig(DemoProperties demoProperties) {
        this.demoProperties = demoProperties;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                                          .title(demoProperties.getTitle())
                                          .description(demoProperties.getDescription())
                                          .version(demoProperties.getVersion()))
                            .path("/public/authentication/login", loginPathItem());
    }

    @Bean
    public GroupedOpenApi demoGroupOpenApi() {
        return GroupedOpenApi.builder()
                             .group("Demo")
                             .packagesToScan("com.example.demo.controller")
                             .build();
    }

    private PathItem loginPathItem() {
        Operation operation = new Operation();
        operation.operationId("login");
        operation.summary("Login a user");
        operation.description("Login a user, and return the authenticating token, along with it's duration");
        operation.addTagsItem("Authentication");
        operation.addParametersItem(new Parameter().name("username")
                                                   .required(true)
                                                   .in("query")
                                                   .schema(new Schema<String>().type("string")));
        operation.addParametersItem(new Parameter().name("password")
                                                   .required(true)
                                                   .in("query")
                                                   .schema(new Schema<String>().type("string")));
        operation.responses(new ApiResponses().addApiResponse("200",
                                                              new ApiResponse().content(
                                                                      new Content()
                                                                              .addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                                                                            new MediaType()
                                                                                                    .schema(new Schema<>()
                                                                                                                    .$ref("#/components/schemas/Token")))))
                                              .addApiResponse("401",
                                                              new ApiResponse()
                                                                      .description("Invalid credentials")
                                                                      .content(
                                                                              new Content()
                                                                                      .addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                                                                                    new MediaType()
                                                                                                            .schema(new Schema<>()
                                                                                                                            .$ref("#/components/schemas/ErrorObject")))))
                                              .addApiResponse("403",
                                                              new ApiResponse()
                                                                      .description("Account not activated")
                                                                      .content(
                                                                              new Content()
                                                                                      .addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                                                                                    new MediaType()
                                                                                                            .schema(new Schema<>()
                                                                                                                            .$ref("#/components/schemas/ErrorObject"))))));

        PathItem pathItem = new PathItem();
        pathItem.post(operation);

        return pathItem;
    }
}
