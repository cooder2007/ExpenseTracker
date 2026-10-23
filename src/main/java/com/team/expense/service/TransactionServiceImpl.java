package com.team.expense.service;

import com.team.expense.dao.TransactionDao;
import com.team.expense.model.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class TransactionServiceImpl implements TransactionService {
    private final TransactionDao dao;

    public TransactionServiceImpl(TransactionDao dao) {
        this.dao = Objects.requireNonNull(dao, "dao cannot be null");
    }

    @Override
    public void addTransaction(Transaction transaction) {
        dao.add(transaction);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return dao.getAll();
    }

    @Override
    public Optional<Transaction> findById(int id) {
        return dao.getById(id);
    }

    @Override
    public void updateTransaction(Transaction transaction) {
        dao.update(transaction);
    }

    @Override
    public void deleteTransaction(int id) {
        dao.delete(id);
    }

    @Override
    public List<Transaction> search(String category, LocalDate from, LocalDate to) {
        String normalizedCategory = category == null ? "" : category.trim().toLowerCase(Locale.ROOT);
        return dao.getAll().stream()
                .filter(transaction -> normalizedCategory.isEmpty()
                        || transaction.getCategory().toLowerCase(Locale.ROOT).contains(normalizedCategory))
                .filter(transaction -> from == null || !transaction.getDate().isBefore(from))
                .filter(transaction -> to == null || !transaction.getDate().isAfter(to))
                .collect(Collectors.toList());
    }
}
