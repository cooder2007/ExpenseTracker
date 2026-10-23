package com.team.expense.dao;

import java.util.List;
import java.util.Optional;

import com.team.expense.model.Transaction;

public interface TransactionDao {
    void add(Transaction transaction);
    List<Transaction> getAll();
    Optional<Transaction> getById(int id);
    void update(Transaction transaction);
    void delete(int id);
}
