package com.libertex.demo.service;

import com.libertex.demo.converter.AccountConverter;
import com.libertex.demo.entity.Account;
import com.libertex.demo.entity.TransactionHistory;
import com.libertex.demo.exception.OperationIsForbiddenException;
import com.libertex.demo.repository.AccountRepository;
import com.libertex.demo.repository.TransactionHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

public class AccountServiceTest {

    private AccountRepository accountRepository = mock(AccountRepository.class);
    private TransactionHistoryService transactionHistoryService;
    private AccountConverter accountConverter = new AccountConverter();

    private Account savedAccount;
    private List<TransactionHistory> historyEntries;

    @BeforeEach
    private void before() {
        historyEntries = new ArrayList<>();
        savedAccount = Account.builder()
                .id(1L)
                .amount(BigDecimal.ZERO)
                .build();
        when(accountRepository.save(any(Account.class))).thenAnswer((Answer) invocation -> {
            Object[] args = invocation.getArguments();
            savedAccount = (Account) args[0];
            return savedAccount;
        });
        var transactionHistoryRepository = mock(TransactionHistoryRepository.class);
        when(transactionHistoryRepository.save(any())).thenAnswer((Answer) invocation -> {
            Object[] args = invocation.getArguments();
            TransactionHistory entry = (TransactionHistory) args[0];
            historyEntries.add(entry);
            return entry;
        });
        when(transactionHistoryRepository.findByAccountId(eq(1L))).thenReturn(historyEntries);

        transactionHistoryService = new TransactionHistoryService(transactionHistoryRepository);

        when(accountRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(savedAccount));
    }

    @Test
    public void testConcurrentDeposit() throws InterruptedException {
        AccountService accountService = new AccountService(accountRepository, transactionHistoryService, accountConverter);
        runWithConcurrency(10, () -> accountService.deposit(1L, BigDecimal.valueOf(1L)));
        assertEquals("Sum should be 10", BigDecimal.valueOf(10L), savedAccount.getAmount());
    }

    @Test
    public void testConcurrentWithdraw() throws InterruptedException {
        AccountService accountService = new AccountService(accountRepository, transactionHistoryService, accountConverter);
        accountService.deposit(1L, BigDecimal.valueOf(15L));

        runWithConcurrency(10, () -> accountService.withdraw(1L, BigDecimal.valueOf(1L)));

        assertEquals("Sum should be 5", BigDecimal.valueOf(5L), savedAccount.getAmount());
    }

    @Test
    public void testConcurrentWithdrawShouldNotWorkWithNonPositiveBalance() throws InterruptedException {
        AccountService accountService = new AccountService(accountRepository, transactionHistoryService, accountConverter);
        accountService.deposit(1L, BigDecimal.valueOf(5));
        AtomicInteger exceptionCount = new AtomicInteger(0);

        runWithConcurrency(10, () -> {
            try {
                accountService.withdraw(1L, BigDecimal.valueOf(1L));
            } catch (OperationIsForbiddenException e) {
                exceptionCount.incrementAndGet();
            }
        });

        assertEquals("Sum should be 0", BigDecimal.valueOf(0L), savedAccount.getAmount());
        assertEquals("Should be 5 failed withdraw attempts", 5, exceptionCount.get());
    }

    private void runWithConcurrency(int numberOfThreads, Runnable runnable) throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(() -> {
                runnable.run();
                latch.countDown();
            });
        }
        latch.await();
    }
}
