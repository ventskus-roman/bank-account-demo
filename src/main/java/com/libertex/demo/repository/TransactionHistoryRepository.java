package com.libertex.demo.repository;

import com.libertex.demo.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
    List<TransactionHistory> findByAccountId(Long accountId);
}
