package com.libertex.demo.controller;

import com.libertex.demo.dto.AccountDto;
import com.libertex.demo.exception.ValidationException;
import com.libertex.demo.service.AccountService;
import com.libertex.demo.validator.AccountValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountValidator validator;

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    public AccountDto getAccount(@PathVariable("id") Long accountId) {
        return accountService.get(accountId);
    }

    @PostMapping(path = "/{id}/deposit", produces = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    public AccountDto deposit(@PathVariable("id") Long accountId, @RequestParam("amount") BigDecimal amount) throws ValidationException {
        validator.validate(accountId, amount);
        return accountService.deposit(accountId, amount);
    }

    @PostMapping(path = "/{id}/withdraw", produces = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    public AccountDto withdraw(@PathVariable("id") Long accountId, @RequestParam("amount") BigDecimal amount) throws ValidationException {
        validator.validate(accountId, amount);
        return accountService.withdraw(accountId, amount);
    }

    //FOR DEMO PURPOSES - we should have possibility to create an account
    @PutMapping(produces = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    public AccountDto create() {
        return accountService.create();
    }
}
