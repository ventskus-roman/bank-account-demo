package com.libertex.demo.service;

import com.libertex.demo.entity.TransactionHistory;
import com.libertex.demo.enums.TransactionType;
import com.libertex.demo.repository.TransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TransactionHistoryService {

    private final TransactionHistoryRepository transactionHistoryRepository;

    public void saveTransaction(Long accountId, BigDecimal amount, TransactionType transactionType) {
        var transaction = TransactionHistory.builder()
                .accountId(accountId)
                .amount(amount)
                .type(transactionType)
                .dateTime(ZonedDateTime.now())
                .build();
        transactionHistoryRepository.save(transaction);
    }

    public List<TransactionHistory> findByAccountId(Long accountId) {
        return transactionHistoryRepository.findByAccountId(accountId);
    }
}
