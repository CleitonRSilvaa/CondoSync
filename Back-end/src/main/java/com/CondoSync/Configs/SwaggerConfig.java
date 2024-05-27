package com.CondoSync.Configs;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("CondoSync API").version("1.0")
                        .description("API para o sistema da CondoSync."))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

    // @Bean
    // public GroupedOpenApi publicApi() {
    // return GroupedOpenApi.builder()
    // .group("springshop-public")
    // .pathsToMatch("/api/**")
    // .build();
    // }

    // private SecurityScheme createAPIKeyScheme() {
    // return new SecurityScheme().type(SecurityScheme.Type.HTTP)
    // .bearerFormat("JWT")
    // .scheme("bearer");
    // }

    // @Bean
    // public OpenAPI springShopOpenAPI() {
    // return new OpenAPI()
    // .info(new Info().title("Spring Shop API")
    // .description("Spring shop sample application")
    // .version("v0.0.1"))
    // .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
    // .components(new io.swagger.v3.oas.models.Components()
    // .addSecuritySchemes("bearerAuth", new SecurityScheme()
    // .name("bearerAuth")
    // .type(SecurityScheme.Type.HTTP)
    // .scheme("bearer")
    // .bearerFormat("JWT")));
    // }

    // @Bean
    // public OpenAPI openAPI() {
    // return new OpenAPI().addSecurityItem(new
    // SecurityRequirement().addList("Bearer Authentication"))
    // .components(new Components().addSecuritySchemes("Bearer Authentication",
    // createAPIKeyScheme()))
    // .info(new Info().title("My REST API")
    // .description("Some custom description of API.")
    // .version("1.0").contact(new Contact().name("Sallo Szrajbman")
    // .email("www.baeldung.com").url("salloszraj@gmail.com"))
    // .license(new License().name("License of API")
    // .url("API license URL")));
    // }

}
