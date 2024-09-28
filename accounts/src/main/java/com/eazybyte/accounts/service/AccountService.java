package com.eazybyte.accounts.service;

import com.eazybyte.accounts.constants.AccountsConstants;
import com.eazybyte.accounts.dto.AccountsDto;
import com.eazybyte.accounts.dto.AccountsMsgDto;
import com.eazybyte.accounts.dto.CustomerDto;
import com.eazybyte.accounts.entity.Accounts;
import com.eazybyte.accounts.entity.Customer;
import com.eazybyte.accounts.exception.CustomerAlreadyExistsException;
import com.eazybyte.accounts.exception.ResourceNotFoundException;
import com.eazybyte.accounts.mapper.AccountsMapper;
import com.eazybyte.accounts.mapper.CustomerMapper;
import com.eazybyte.accounts.repository.AccountRepository;
import com.eazybyte.accounts.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import org.springframework.util.ObjectUtils;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService {

    private AccountRepository accountRepository;
    private CustomerRepository customerRepository;
    private final StreamBridge streamBridge;

    public void createAccount(CustomerDto customerDto){
        Customer customer = CustomerMapper.mapToCustomer(customerDto);

        customer.setCreatedBy("Anonymous");
        customer.setCreatedAt(LocalDateTime.now());

        Optional<Customer> optionalCustomer = customerRepository.findByMobileNumber(customerDto.getMobileNumber());
        if(optionalCustomer.isPresent()){
            throw new CustomerAlreadyExistsException("Customer already registered with given mobileNumber");
        }
        Customer savedCustomer = customerRepository.save(customer);
        Accounts savedAccount = accountRepository.save(createNewAccount(savedCustomer));
        sendCommunication(savedAccount, savedCustomer);
    }

    private Accounts createNewAccount(Customer customer){

        long randomAccNumber = 1000000000L + new Random().nextInt(900000000);

        return Accounts.builder()
                .customerId(customer.getCustomerId())
                .accountNumber(randomAccNumber)
                .branchAddress(AccountsConstants.ADDRESS)
                .accountType(AccountsConstants.SAVINGS)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public CustomerDto searchCustomer(String mobileNumber){
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        Accounts accounts = accountRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString())
        );

        CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer);
        customerDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts));
        return customerDto;
    }

    public boolean updateAccount(CustomerDto customerDto) {
        boolean isUpdated = false;

        AccountsDto accountsDto = customerDto.getAccountsDto();
        if (accountsDto != null) {
            Accounts accounts = accountRepository.findById(accountsDto.getAccountNumber()).orElseThrow(
                    () -> new ResourceNotFoundException("Account", "Account number", accountsDto.getAccountNumber().toString())
            );

            AccountsMapper.mapToAccounts(accountsDto);
            accounts = accountRepository.save(accounts);

            Long customerId = accounts.getCustomerId();
            Customer customer = customerRepository.findById(customerId).orElseThrow(
                    () -> new ResourceNotFoundException("Customer", "Customer ID", customerId.toString())
            );

            customerRepository.save(CustomerMapper.mapToCustomer(customerDto));
            isUpdated = true;
        }
        return isUpdated;
    }

    public boolean deleteAccount(String mobileNumber){
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        accountRepository.deleteByCustomerId(customer.getCustomerId());
        customerRepository.deleteById(customer.getCustomerId());
        return true;
    }

    private void sendCommunication(Accounts accounts, Customer customer){
        var accountsMsgDto = new AccountsMsgDto(accounts.getAccountNumber(), customer.getName(),
            customer.getEmail(), customer.getMobileNumber());
        log.info("sending communication request for the details: {}", accountsMsgDto);
        var result = streamBridge.send("sendCommunication-out-0", accountsMsgDto);
        log.info("sending communication result: {}", result);

    }

    public boolean updateCommunicationStatus(Long accountNumber){
        boolean isUpdated = false;
        if(accountNumber != null){
            Accounts accounts = accountRepository.findById(accountNumber).orElseThrow(
                () -> new ResourceNotFoundException("Account", "accountNumber", accountNumber.toString())
            );
            accounts.setCommunicationSw(true);
            accountRepository.save(accounts);
            isUpdated = true;
        }
        return isUpdated;
    }
}
