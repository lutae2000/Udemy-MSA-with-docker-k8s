package com.eazybyte.accounts;

import com.eazybyte.accounts.dto.AccountsContactInfoDto;
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
/*@ComponentScans({ @ComponentScan("com.eazybytes.accounts.controller") })
@EnableJpaRepositories("com.eazybytes.accounts.repository")
@EntityScan("com.eazybytes.accounts.model")*/
@EnableJpaAuditing(auditorAwareRef = "auditAware")
@OpenAPIDefinition(
        info = @Info(
                title = "Accounts microservice REST API Documentation",
                description = "Account microservice API Documentation",
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
                description = "(External) Account microservice API Documentation",
                url = "http://localhost:8080/swagger-ui/index.html"
        )
)
@EnableConfigurationProperties(value = {AccountsContactInfoDto.class})
public class AccountsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountsApplication.class, args);
    }

}
