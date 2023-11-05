package com.eazybyte.accounts.service;

import com.eazybyte.accounts.dto.CardsDto;
import com.eazybyte.accounts.dto.CustomerDetailsDto;
import com.eazybyte.accounts.dto.LoansDto;
import com.eazybyte.accounts.entity.Accounts;
import com.eazybyte.accounts.entity.Customer;
import com.eazybyte.accounts.exception.ResourceNotFoundException;
import com.eazybyte.accounts.mapper.AccountsMapper;
import com.eazybyte.accounts.mapper.CustomerMapper;
import com.eazybyte.accounts.repository.AccountRepository;
import com.eazybyte.accounts.repository.CustomerRepository;
import com.eazybyte.accounts.service.client.CardFeignClient;
import com.eazybyte.accounts.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerService {

    private AccountRepository accountRepository;
    private CustomerRepository customerRepository;
    private CardFeignClient cardFeignClient;
    private LoansFeignClient loansFeignClient;
    public CustomerDetailsDto fetchCustomerDetails(String correlationId, String mobileNumber){
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
            () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );

        Accounts accounts = accountRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
            () -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString())
        );

        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer);
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts));

        ResponseEntity<LoansDto> loansDtoResponseEntity = loansFeignClient.fetchLoanDetails(correlationId, mobileNumber);
        if(null != loansDtoResponseEntity){
            customerDetailsDto.setLoansDto(loansDtoResponseEntity.getBody());
        }

        ResponseEntity<CardsDto> cardsDtoResponseEntity = cardFeignClient.fetchCardDetails(correlationId, mobileNumber);
        if(null != cardsDtoResponseEntity){
            customerDetailsDto.setCardsDto(cardsDtoResponseEntity.getBody());
        }

        return customerDetailsDto;
    }
}
