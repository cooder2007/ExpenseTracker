package com.team.expense.dao;

import com.team.expense.model.Transaction;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryTransactionDao implements TransactionDao {
    private final Map<Integer, Transaction> transactions = new LinkedHashMap<>();
    private int nextId = 1;

    @Override
    public synchronized void add(Transaction transaction) {
        if (transaction == null) throw new IllegalArgumentException("Transaction cannot be null.");
        Transaction copy = copyOf(transaction);
        int id = copy.getId();
        if (id <= 0) {
            id = nextId++;
            copy.setId(id);
            transaction.setId(id);
        } else {
            if (transactions.containsKey(id)) throw new IllegalArgumentException("Transaction ID already exists: " + id);
            nextId = Math.max(nextId, id + 1);
        }
        transactions.put(id, copy);
    }

    @Override
    public synchronized List<Transaction> getAll() {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions.values()) result.add(copyOf(transaction));
        result.sort(Comparator.comparing(Transaction::getDate).reversed().thenComparing(Transaction::getId));
        return result;
    }

    @Override
    public synchronized Optional<Transaction> getById(int id) {
        Transaction transaction = transactions.get(id);
        return transaction == null ? Optional.empty() : Optional.of(copyOf(transaction));
    }

    @Override
    public synchronized void update(Transaction transaction) {
        if (transaction == null) throw new IllegalArgumentException("Transaction cannot be null.");
        if (transaction.getId() <= 0 || !transactions.containsKey(transaction.getId())) {
            throw new IllegalArgumentException("Cannot update: transaction not found.");
        }
        transactions.put(transaction.getId(), copyOf(transaction));
    }

    @Override
    public synchronized void delete(int id) {
        if (transactions.remove(id) == null) throw new IllegalArgumentException("Cannot delete: transaction not found.");
    }

    private Transaction copyOf(Transaction t) {
        return new Transaction(t.getId(), t.getType(), t.getAmount(), t.getCategory(), t.getDate(), t.getNote());
    }
}
