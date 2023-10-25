package com.eazybyte.accounts.mapper;

import com.eazybyte.accounts.dto.CustomerDetailsDto;
import com.eazybyte.accounts.dto.CustomerDto;
import com.eazybyte.accounts.entity.Customer;

import java.time.LocalDateTime;

public class CustomerMapper {

    public static CustomerDto mapToCustomerDto(Customer customer){
        return CustomerDto.builder()
                .name(customer.getName())
                .email(customer.getEmail())
                .mobileNumber(customer.getMobileNumber())
                .build();
    }

    public static Customer mapToCustomer(CustomerDto customerDto){
        return Customer.builder()
                .name(customerDto.getName())
                .email(customerDto.getEmail())
                .mobileNumber(customerDto.getMobileNumber())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static CustomerDetailsDto mapToCustomerDetailsDto(Customer customer){
        return CustomerDetailsDto.builder()
            .name(customer.getName())
            .email(customer.getEmail())
            .mobileNumber(customer.getMobileNumber())
            .build();

    }
}
