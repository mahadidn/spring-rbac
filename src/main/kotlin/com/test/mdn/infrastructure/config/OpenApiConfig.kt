package com.test.mdn.infrastructure.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import io.swagger.v3.oas.models.servers.ServerVariable
import io.swagger.v3.oas.models.servers.ServerVariables
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        val schemeName = "bearerAuth"
        return OpenAPI()
            .info(
                Info()
                    .title("Test MDN API")
                    .version("1.0.0")
                    .description("Test MDN System")
                    .contact(
                        Contact()
                            .name("MDN Test")
                            .email("mahadidwinugraha@gmail.com")
                    )
            )
            .servers(
                listOf(
                    Server()
                        .url("{baseUrl}")
                        .description("Custom Server")
                        .variables(
                            ServerVariables().addServerVariable(
                                "baseUrl",
                                ServerVariable()
                                    ._default("http://localhost:8080")
                                    .description("API base URL")
                            )
                        ),
                )
            )
            .components(
                Components().addSecuritySchemes(
                    "bearerAuth",
                    SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT Bearer token")
                )
            )
            .addSecurityItem(SecurityRequirement().addList(schemeName))
    }
}
