package com.example.loans;

import com.example.loans.dto.LoansContactsDto;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAware")
@OpenAPIDefinition(
        info = @Info(
                title = "Cards microservice REST API Documentation",
                description = "Cards microservice API Documentation",
                version = "v1",
                contact = @Contact(
                        name = "utlee",
                        email = "lutae2000@gmail.com"
                ),
                license = @License(
                        name = "Aphache 2.0",
                        url = "https://utlee.duckdns.org"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "(External) Card microservice API Documentation",
                url = "http://localhost:8080/swagger-ui/index.html"
        )
)
@EnableConfigurationProperties(value = {LoansContactsDto.class})
public class LoansApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoansApplication.class, args);
    }

}
