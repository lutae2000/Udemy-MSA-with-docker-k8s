package com.eazybyte.accounts.functions;

import com.eazybyte.accounts.service.AccountService;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AccountFunctions {

    @Bean
    public Consumer<Long> updateCommunication(AccountService accountService) {
        return account ->{
            log.info("Updating account communication status for the account number: {}", account.toString());
            accountService.updateCommunicationStatus(account);
        };
    }
}
