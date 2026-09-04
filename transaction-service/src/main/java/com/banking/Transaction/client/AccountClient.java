package com.banking.Transaction.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.banking.Transaction.config.AccountFeignConfig;
import com.banking.Transaction.model.AccountResponse;

@FeignClient(
        name = "account-service",
        configuration = AccountFeignConfig.class
)
public interface AccountClient {

    @GetMapping("/accounts/number/{accountNumber}")
    AccountResponse getAccount(
            @PathVariable String accountNumber);

    @PatchMapping("/accounts/{accountNumber}/deposit")
    AccountResponse deposit(
            @PathVariable String accountNumber,
            @RequestParam Double amount);

    @PatchMapping("/accounts/{accountNumber}/withdraw")
    AccountResponse withdraw(
            @PathVariable String accountNumber,
            @RequestParam Double amount);
}