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
@Table(name = "Dividends")
public class Dividend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dividend_id")
    private Long dividendId;

    @Column(name = "stock_id")
    private Long stockId;

    @Transient
    private String stockTicker;

    @Column(name = "exchange_id")
    private Long exchangeId;

    @Transient
    private String exchange;

    @Column(name = "ex_date")
    private String exDate;

    @Column(name = "pay_date")
    private String payDate;

    @Column
    private String payout;

    @Override
    public String toString() {
        return "Dividend{" +
                "dividendId=" + dividendId +
                ", stockId=" + stockId +
                ", stockTicker='" + stockTicker + '\'' +
                ", exchangeId=" + exchangeId +
                ", exchange='" + exchange + '\'' +
                ", exDate='" + exDate + '\'' +
                ", payDate='" + payDate + '\'' +
                ", payout='" + payout + '\'' +
                '}';
    }
}