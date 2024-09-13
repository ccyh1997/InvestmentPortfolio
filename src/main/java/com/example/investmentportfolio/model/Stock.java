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
@Table(name = "Stocks")
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long stockId;

    @Column(name = "stock_ticker")
    private String stockTicker;

    @Column(name = "stock_name")
    private String stockName;

    @Column(name = "stock_type")
    private String stockType;

    @Column(name = "exchange_id")
    private Long exchangeId;

    @Transient
    private String exchange;

    @Column(name = "last_price")
    private String lastPrice;

    @Column(name = "base_currency")
    private String baseCurrency;

    @Column(name = "div_ind")
    private String divInd;

    @Column(name = "delist_ind")
    private String delistInd;
}
