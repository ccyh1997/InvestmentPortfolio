package com.example.investmentportfolio.repository;

import com.example.investmentportfolio.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);
    @Query(value = "SELECT * FROM transactions WHERE user_id = ?1 AND stock_id = ?2 AND transaction_date <= CAST(?3 AS DATE)", nativeQuery = true)
    List<Transaction> findByUserIdAndStockIdAndDate(Long userId, Long stockId, String date);
    @Query(value = "SELECT transaction_date FROM transactions WHERE user_id = ?1 AND stock_id = ?2 ORDER BY transaction_date ASC LIMIT 1", nativeQuery = true)
    String getEarliestTransactionDate(Long userId, Long stockId);
    @Query(value = "SELECT * FROM transactions WHERE user_id = ?1 AND stock_id = ?2 AND transaction_type = 'Buy'", nativeQuery = true)
    List<Transaction> getBuyTransactionsByStock(Long userId, Long stockId);
    @Query(value = "SELECT * FROM transactions WHERE user_id = ?1 AND stock_id = ?2 AND transaction_type = 'Sell'", nativeQuery = true)
    List<Transaction> getSellTransactionsByStock(Long userId, Long stockId);
    @Query(value = "SELECT * FROM transactions WHERE user_id = ?1 AND stock_id = ?2 AND transaction_type = 'Buy' AND transaction_date <= CAST(?3 AS DATE)", nativeQuery = true)
    List<Transaction> getBuyTransactionsByUserIdAndStockIdAndDate(Long userId, Long stockId, String date);
}