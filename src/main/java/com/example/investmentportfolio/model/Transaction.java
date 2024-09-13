package com.example.investmentportfolio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "user_id")
    private Long userId;

    @Transient
    private String username;

    @Column(name = "transaction_date")
    private String transactionDate;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "stock_id")
    private Long stockId;

    @Transient
    private String stockTicker;

    @Transient
    private String exchange;

    @Column
    private String units;

    @Column(name = "unit_price")
    private String unitPrice;

    @Column
    private String currency;

    @Column
    private String fees;
}
