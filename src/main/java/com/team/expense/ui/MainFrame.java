package com.team.expense.ui;

import com.team.expense.model.Transaction;
import com.team.expense.model.TransactionType;
import com.team.expense.service.TransactionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MainFrame extends JFrame {
    private final TransactionService service;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JLabel summaryLabel;

    public MainFrame(TransactionService service) {
        super("ExpenseTracer");
        this.service = service;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("ExpenseTracer — Income & Expense Manager", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(20f));
        title.setBorder(BorderFactory.createEmptyBorder(14, 8, 8, 8));
        add(title, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Type", "Amount", "Category", "Date", "Note"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton addButton = new JButton("Add Transaction");
        JButton editButton = new JButton("Edit Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton refreshButton = new JButton("Refresh");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(addButton);
        buttons.add(editButton);
        buttons.add(deleteButton);
        buttons.add(refreshButton);

        summaryLabel = new JLabel(" ");
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        bottom.add(buttons, BorderLayout.CENTER);
        bottom.add(summaryLabel, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addTransaction());
        editButton.addActionListener(e -> editTransaction());
        deleteButton.addActionListener(e -> deleteTransaction());
        refreshButton.addActionListener(e -> refreshTable());

        refreshTable();
    }

    public void refreshTable() {
        try {
            tableModel.setRowCount(0);
            List<Transaction> transactions = service.getAllTransactions();
            double income = 0, expenses = 0;
            for (Transaction t : transactions) {
                tableModel.addRow(new Object[]{t.getId(), t.getType(), String.format("%.2f", t.getAmount()),
                        t.getCategory(), t.getDate(), t.getNote()});
                if (t.getType() == TransactionType.INCOME) income += t.getAmount();
                else expenses += t.getAmount();
            }
            summaryLabel.setText(String.format("  Income: %.2f     Expenses: %.2f     Balance: %.2f", income, expenses, income - expenses));
        } catch (RuntimeException ex) {
            showError("Could not load transactions: " + ex.getMessage());
        }
    }

    private void addTransaction() {
        Transaction t = showTransactionDialog(null);
        if (t == null) return;
        try {
            service.addTransaction(t);
            refreshTable();
        } catch (RuntimeException ex) { showError(ex.getMessage()); }
    }

    private void editTransaction() {
        Transaction selected = selectedTransaction();
        if (selected == null) { showError("Select a transaction first."); return; }
        Transaction edited = showTransactionDialog(selected);
        if (edited == null) return;
        try {
            service.updateTransaction(edited);
            refreshTable();
        } catch (RuntimeException ex) { showError(ex.getMessage()); }
    }

    private void deleteTransaction() {
        Transaction selected = selectedTransaction();
        if (selected == null) { showError("Select a transaction first."); return; }
        int answer = JOptionPane.showConfirmDialog(this, "Delete transaction #" + selected.getId() + "?",
                "Confirm deletion", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            try { service.deleteTransaction(selected.getId()); refreshTable(); }
            catch (RuntimeException ex) { showError(ex.getMessage()); }
        }
    }

    private Transaction selectedTransaction() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        int id = (int) tableModel.getValueAt(table.convertRowIndexToModel(row), 0);
        return service.findById(id).orElse(null);
    }

    private Transaction showTransactionDialog(Transaction existing) {
        JComboBox<TransactionType> typeBox = new JComboBox<>(TransactionType.values());
        JTextField amountField = new JTextField(12);
        JTextField categoryField = new JTextField(16);
        JTextField dateField = new JTextField(LocalDate.now().toString(), 12);
        JTextField noteField = new JTextField(20);

        if (existing != null) {
            typeBox.setSelectedItem(existing.getType());
            amountField.setText(Double.toString(existing.getAmount()));
            categoryField.setText(existing.getCategory());
            dateField.setText(existing.getDate().toString());
            noteField.setText(existing.getNote());
        }

        JPanel form = new JPanel(new java.awt.GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Type:")); form.add(typeBox);
        form.add(new JLabel("Amount:")); form.add(amountField);
        form.add(new JLabel("Category:")); form.add(categoryField);
        form.add(new JLabel("Date (YYYY-MM-DD):")); form.add(dateField);
        form.add(new JLabel("Note:")); form.add(noteField);

        int result = JOptionPane.showConfirmDialog(this, form,
                existing == null ? "Add Transaction" : "Edit Transaction",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return null;

        try {
            double amount = new BigDecimal(amountField.getText().trim()).doubleValue();
            Transaction transaction = new Transaction(
                    existing == null ? 0 : existing.getId(),
                    (TransactionType) typeBox.getSelectedItem(),
                    amount,
                    categoryField.getText(),
                    LocalDate.parse(dateField.getText().trim()),
                    noteField.getText());
            return transaction;
        } catch (DateTimeParseException | IllegalArgumentException ex) {
            showError("Check the amount, category, and date. Date must use YYYY-MM-DD.");
            return null;
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message == null ? "An unexpected error occurred." : message,
                "ExpenseTracer", JOptionPane.ERROR_MESSAGE);
    }
}
