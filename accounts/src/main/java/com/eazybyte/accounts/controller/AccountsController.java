package com.eazybyte.accounts.controller;

import com.eazybyte.accounts.constants.AccountsConstants;
import com.eazybyte.accounts.dto.AccountsContactInfoDto;
import com.eazybyte.accounts.dto.CustomerDto;
import com.eazybyte.accounts.dto.ErrorResponseDto;
import com.eazybyte.accounts.dto.ResponseDto;
import com.eazybyte.accounts.service.AccountService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "CRUD REST API for Accounts",
        description = "CRUD REST API details"
)
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class AccountsController {

    private AccountService accountService;

    public AccountsController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Value("${build.version}")
    private String buildVersion;


    @Autowired
    private AccountsContactInfoDto accountsContactInfoDto;

    @Operation(
            summary = "create account API",
            description = "create new customer & account API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "HTTP Status created"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal server error"
            )
    })
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createAccount(@RequestBody @Valid CustomerDto customerDto) {

        accountService.createAccount(customerDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDto(AccountsConstants.STATUS_201, AccountsConstants.MESSAGE_201));
    }

    @Operation(
            summary = "search account API",
            description = "search account API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status ok"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal server error"
            )
    })
    @GetMapping("/fetch")
    public ResponseEntity<CustomerDto> fetchAccount(@RequestParam String mobileNumber){
        CustomerDto customerDto = accountService.searchCustomer(mobileNumber);
        return ResponseEntity.status(HttpStatus.OK)
                .body(customerDto);
    }

    @Operation(
            summary = "search account API",
            description = "search account API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status ok"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal server error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateAccountDetails(@RequestBody CustomerDto customerDto){
        boolean isUpdated = accountService.updateAccount(customerDto);
        if(isUpdated){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto(AccountsConstants.STATUS_500, AccountsConstants.MESSAGE_500));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteAccountDetails(@RequestParam String mobileNumber){
        boolean isDeleted = accountService.deleteAccount(mobileNumber);
        if(isDeleted){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(AccountsConstants.STATUS_500, AccountsConstants.MESSAGE_500));
        }
    }

    @GetMapping("/build-info")
    @Retry(name = "getBuildInfo", fallbackMethod = "getBuildInfoFallback")
    public ResponseEntity<String> getBuildInfo() throws TimeoutException {
        log.debug("getBuildInfo() method Invoked");
        throw new TimeoutException();
//        return ResponseEntity.status(HttpStatus.OK).body(buildVersion);
    }

    public ResponseEntity<String> getBuildInfoFallback(Throwable throwable){
        log.debug("getBuildInfoFallback() method Invoked");
        return ResponseEntity.status(HttpStatus.OK).body("0.9");
    }

    @GetMapping("/account-info")
    @RateLimiter(name = "getContactInfo", fallbackMethod = "getContactInfoFallback")
    public ResponseEntity<AccountsContactInfoDto> getContactInfo(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(accountsContactInfoDto);
    }

    public ResponseEntity<String> getContactInfoFallback(Throwable throwable){
        return ResponseEntity.status(HttpStatus.OK)
            .body("getContactInfoFallback response limit");
    }

}
