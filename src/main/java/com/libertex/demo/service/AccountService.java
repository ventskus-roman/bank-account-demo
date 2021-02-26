package com.libertex.demo.service;

import com.libertex.demo.converter.AccountConverter;
import com.libertex.demo.dto.AccountDto;
import com.libertex.demo.entity.Account;
import com.libertex.demo.enums.TransactionType;
import com.libertex.demo.exception.OperationIsForbiddenException;
import com.libertex.demo.exception.RecordNotFoundException;
import com.libertex.demo.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.collections.ManagedConcurrentWeakHashMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.Predicate;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionHistoryService transactionHistoryService;
    private final AccountConverter accountConverter;

    private final Map<Long, Long> accountLockMap = new WeakHashMap<>();

    public AccountDto get(Long accountId) {
        return accountRepository.findById(accountId)
                .map(accountConverter::convert)
                .orElseThrow(() -> new RecordNotFoundException(Account.class, accountId));
    }

    public AccountDto deposit(Long accountId, BigDecimal amount) {
        var lock = getLock(accountId);
        //locks only operation within the same accountId
        synchronized (lock) {
            return updateBalanceIfBalance(accountId, amount, TransactionType.DEPOSIT, (total) -> true)
                    .map(accountConverter::convert)
                    .orElseThrow(() -> new OperationIsForbiddenException("Operation is forbidden"));
        }
    }

    public AccountDto withdraw(Long accountId, BigDecimal amount) {
        var lock = getLock(accountId);
        //locks only operation within the same accountId
        synchronized (lock) {
            //test if balance after operation will be greater or equals 0
            return updateBalanceIfBalance(accountId, amount, TransactionType.WITHDRAW, (total) -> total.compareTo(amount) >= 0)
                    .map(accountConverter::convert)
                    .orElseThrow(() -> new OperationIsForbiddenException("Not enough money of the account for the operation"));
        }
    }

    private synchronized Object getLock(Long accountId) {
        var lock = accountLockMap.get(accountId);
        if (lock == null) {
            accountLockMap.put(accountId, accountId);
            lock = accountId;
        }
        return lock;
    }

    private Optional<Account> updateBalanceIfBalance(Long accountId, BigDecimal newAmount, TransactionType transactionType, Predicate<BigDecimal> predicate) {
        var totalBeforeTheOperation = transactionHistoryService.findByAccountId(accountId).stream()
                .map(entry -> TransactionType.DEPOSIT.equals(entry.getType())
                        ? entry.getAmount()
                        : entry.getAmount().negate())
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
        if (predicate.test(totalBeforeTheOperation)) {
            transactionHistoryService.saveTransaction(accountId, newAmount, transactionType);
            var finalAmount = totalBeforeTheOperation.add(TransactionType.DEPOSIT.equals(transactionType)
                    ? newAmount
                    : newAmount.negate());
            return accountRepository.findById(accountId)
                    .map(it -> {
                        it.setAmount(finalAmount);
                        return it;
                    })
                    .map(accountRepository::save);
        } else {
            return Optional.empty();
        }
    }

    public AccountDto create() {
        var account = accountRepository.save(Account.builder()
                .amount(BigDecimal.ZERO)
                .build());
        return accountConverter.convert(account);
    }
}
