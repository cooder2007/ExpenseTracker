package com.team.expense.dao;

import com.team.expense.model.Transaction;
import com.team.expense.model.TransactionType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTransactionDaoTest {
    private Transaction sample() {
        return new Transaction(TransactionType.EXPENSE, 120.50, "Food", LocalDate.now(), "Lunch");
    }

    @Test
    void addAssignsIdAndCanFindTransaction() {
        InMemoryTransactionDao dao = new InMemoryTransactionDao();
        Transaction transaction = sample();
        dao.add(transaction);
        assertTrue(transaction.getId() > 0);
        assertEquals("Food", dao.getById(transaction.getId()).orElseThrow().getCategory());
    }

    @Test
    void updateChangesStoredTransaction() {
        InMemoryTransactionDao dao = new InMemoryTransactionDao();
        Transaction transaction = sample();
        dao.add(transaction);
        transaction.setCategory("Travel");
        dao.update(transaction);
        assertEquals("Travel", dao.getById(transaction.getId()).orElseThrow().getCategory());
    }

    @Test
    void deleteRemovesTransaction() {
        InMemoryTransactionDao dao = new InMemoryTransactionDao();
        Transaction transaction = sample();
        dao.add(transaction);
        dao.delete(transaction.getId());
        assertTrue(dao.getById(transaction.getId()).isEmpty());
    }

    @Test
    void rejectsNonPositiveAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction(TransactionType.EXPENSE, 0, "Food", LocalDate.now(), ""));
    }
}
