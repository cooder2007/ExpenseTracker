package com.team.expense.model;

import java.time.LocalDate;
import java.util.Objects;

public class Transaction {
    private int id;
    private TransactionType type;
    private double amount;
    private String category;
    private LocalDate date;
    private String note;

    public Transaction() {
        this.date = LocalDate.now();
    }

    public Transaction(TransactionType type, double amount, String category,
                       LocalDate date, String note) {
        this(0, type, amount, category, date, note);
    }

    public Transaction(int id, TransactionType type, double amount, String category,
                       LocalDate date, String note) {
        this.id = id;
        this.type = Objects.requireNonNull(type, "type cannot be null");
        setAmount(amount);
        setCategory(category);
        this.date = Objects.requireNonNull(date, "date cannot be null");
        this.note = note == null ? "" : note.trim();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) {
        this.type = Objects.requireNonNull(type, "type cannot be null");
    }

    public double getAmount() { return amount; }
    public void setAmount(double amount) {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Amount must be a positive number.");
        }
        this.amount = amount;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty.");
        }
        this.category = category.trim();
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) {
        this.date = Objects.requireNonNull(date, "date cannot be null");
    }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note == null ? "" : note.trim(); }

    @Override
    public String toString() {
        return "Transaction{id=" + id + ", type=" + type + ", amount=" + amount
                + ", category='" + category + "', date=" + date + ", note='" + note + "'}";
    }
}
