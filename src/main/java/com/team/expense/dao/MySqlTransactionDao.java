package com.team.expense.dao;

import com.team.expense.model.Transaction;
import com.team.expense.model.TransactionType;
import com.team.expense.util.DbConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySqlTransactionDao implements TransactionDao {
    @Override
    public void add(Transaction transaction) {
        String sql = "INSERT INTO transactions (type, amount, category, transaction_date, note) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fillStatement(statement, transaction);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) transaction.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not add transaction.", e);
        }
    }

    @Override
    public List<Transaction> getAll() {
        String sql = "SELECT id, type, amount, category, transaction_date, note FROM transactions ORDER BY transaction_date DESC, id DESC";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) transactions.add(mapRow(result));
            return transactions;
        } catch (SQLException e) {
            throw new DataAccessException("Could not load transactions.", e);
        }
    }

    @Override
    public Optional<Transaction> getById(int id) {
        String sql = "SELECT id, type, amount, category, transaction_date, note FROM transactions WHERE id = ?";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(mapRow(result)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not find transaction.", e);
        }
    }

    @Override
    public void update(Transaction transaction) {
        String sql = "UPDATE transactions SET type=?, amount=?, category=?, transaction_date=?, note=? WHERE id=?";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fillStatement(statement, transaction);
            statement.setInt(6, transaction.getId());
            if (statement.executeUpdate() == 0) throw new IllegalArgumentException("Transaction not found: " + transaction.getId());
        } catch (SQLException e) {
            throw new DataAccessException("Could not update transaction.", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM transactions WHERE id=?";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            if (statement.executeUpdate() == 0) throw new IllegalArgumentException("Transaction not found: " + id);
        } catch (SQLException e) {
            throw new DataAccessException("Could not delete transaction.", e);
        }
    }

    private void fillStatement(PreparedStatement statement, Transaction transaction) throws SQLException {
        if (transaction == null) throw new IllegalArgumentException("Transaction cannot be null.");
        statement.setString(1, transaction.getType().name());
        statement.setBigDecimal(2, java.math.BigDecimal.valueOf(transaction.getAmount()));
        statement.setString(3, transaction.getCategory());
        statement.setDate(4, Date.valueOf(transaction.getDate()));
        statement.setString(5, transaction.getNote());
    }

    private Transaction mapRow(ResultSet result) throws SQLException {
        return new Transaction(
                result.getInt("id"),
                TransactionType.valueOf(result.getString("type")),
                result.getBigDecimal("amount").doubleValue(),
                result.getString("category"),
                result.getDate("transaction_date").toLocalDate(),
                result.getString("note"));
    }

    public static class DataAccessException extends RuntimeException {
        public DataAccessException(String message, Throwable cause) { super(message, cause); }
    }
}
