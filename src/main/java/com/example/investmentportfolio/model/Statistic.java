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
@Table(name = "Statistics")
public class Statistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statistic_id")
    private Long statisticId;

    @Column(name = "user_id")
    private Long userId;

    @Transient
    private String username;

    @Column(name = "stock_id")
    private Long stockId;

    @Transient
    private String stockTicker;

    @Transient
    private String exchange;

    @Column(name = "total_units")
    private String totalUnits;

    @Column(name = "total_cost")
    private String totalCost;

    @Column(name = "total_value")
    private String totalValue;

    @Column(name = "realized_profits")
    private String realizedProfits;

    @Column(name = "unrealized_profits")
    private String unrealizedProfits;

    @Column(name = "dividends_earned")
    private String dividendsEarned;

    @Column(name = "total_profits")
    private String totalProfits;
}