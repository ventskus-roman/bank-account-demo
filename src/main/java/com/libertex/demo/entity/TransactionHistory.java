package com.libertex.demo.entity;

import com.libertex.demo.enums.TransactionType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "transaction_history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private ZonedDateTime dateTime;
}
