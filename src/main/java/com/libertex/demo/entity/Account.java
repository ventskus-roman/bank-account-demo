package com.libertex.demo.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "account")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;
}
