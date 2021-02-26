package com.libertex.demo.converter;

import com.libertex.demo.dto.AccountDto;
import com.libertex.demo.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountConverter {
    public AccountDto convert(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .balance(account.getAmount())
                .build();
    }
}
