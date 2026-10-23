package com.team.expense.service;

import com.team.expense.dao.InMemoryTransactionDao;
import com.team.expense.model.Transaction;
import com.team.expense.model.TransactionType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionServiceImplTest {
    private final TransactionServiceImpl service =
            new TransactionServiceImpl(new InMemoryTransactionDao());

    @Test
    void delegatesCrudOperationsToDao() {
        Transaction transaction = new Transaction(TransactionType.EXPENSE, 24.5,
                "Groceries", LocalDate.of(2025, 3, 4), "Market");

        service.addTransaction(transaction);
        assertTrue(transaction.getId() > 0);
        assertEquals("Groceries", service.findById(transaction.getId()).orElseThrow().getCategory());

        transaction.setCategory("Food");
        service.updateTransaction(transaction);
        assertEquals("Food", service.getAllTransactions().get(0).getCategory());

        service.deleteTransaction(transaction.getId());
        assertTrue(service.findById(transaction.getId()).isEmpty());
    }

    @Test
    void searchFiltersCategoryAndInclusiveDateRange() {
        service.addTransaction(new Transaction(TransactionType.EXPENSE, 20,
                "Food", LocalDate.of(2025, 3, 4), ""));
        service.addTransaction(new Transaction(TransactionType.INCOME, 200,
                "Salary", LocalDate.of(2025, 3, 5), ""));
        service.addTransaction(new Transaction(TransactionType.EXPENSE, 10,
                "Food supplies", LocalDate.of(2025, 3, 6), ""));

        List<Transaction> results = service.search("fOoD", LocalDate.of(2025, 3, 4),
                LocalDate.of(2025, 3, 6));

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(transaction ->
                transaction.getCategory().toLowerCase(Locale.ROOT).contains("food")));
    }

    @Test
    void emptyCategorySearchReturnsAllTransactions() {
        service.addTransaction(new Transaction(TransactionType.INCOME, 100,
                "Salary", LocalDate.now(), ""));

        assertEquals(1, service.search(null, null, null).size());
    }
}
