package com.schwab.aiengineering.urlshortener.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI urlShortenerOpenApi() {

        return new OpenAPI()

                .info(new Info()

                        .title("AI Assisted URL Shortener API")

                        .description("Charles Schwab AI Engineering Assessment")

                        .version("1.0.0")

                        .contact(new Contact()

                                .name("Lakshmi Prasanna")

                                .email("lakshmi.prasanna123@gmail.com"))

                        .license(new License()

                                .name("MIT")))

                .externalDocs(

                        new ExternalDocumentation()

                                .description("Project Documentation")

                                .url("https://github.com/lakshmip2/ai-assisted-software-engineering-url-shortener"));

    }

}