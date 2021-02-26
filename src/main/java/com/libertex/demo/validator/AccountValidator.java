package com.libertex.demo.validator;

import com.libertex.demo.exception.ValidationException;
import com.libertex.demo.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class AccountValidator {

    private final AccountRepository accountRepository;

    //For the side of performance it can be not the best idea to call additional db query just to check, if user exists
    //Also it would be better to refactor validator to make it return all errors, not by one
    public void validate(Long accountId, BigDecimal amount) throws ValidationException {
        if (accountId == null) {
            throw new ValidationException("Account id can't be null");
        }
        if (!accountRepository.existsById(accountId)) {
            throw new ValidationException("Account with id " + accountId + " not found");
        }
        if (BigDecimal.ZERO.compareTo(amount) >= 0) {
            throw new ValidationException("Amount have to be greater than 0");
        }
    }
}
