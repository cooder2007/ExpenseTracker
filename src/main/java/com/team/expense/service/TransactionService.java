package com.team.expense.service;

import com.team.expense.model.Transaction;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionService 
{
    void addTransaction(Transaction transaction);
    List<Transaction> getAllTransactions();
    Optional<Transaction> findById(int id);
    void updateTransaction(Transaction transaction);
    void deleteTransaction(int id);
    List<Transaction> search(String category, LocalDate from, LocalDate to);
}