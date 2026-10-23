package com.team.expense;

import com.team.expense.dao.InMemoryTransactionDao;
import com.team.expense.dao.MySqlTransactionDao;
import com.team.expense.dao.TransactionDao;
import com.team.expense.service.TransactionService;
import com.team.expense.service.TransactionServiceImpl;
import com.team.expense.ui.MainFrame;
import com.team.expense.util.DbConnection;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        TransactionDao dao;
        if (DbConnection.isConfigured()) {
            dao = new MySqlTransactionDao();
            System.out.println("ExpenseTracer starting with MySQL storage...");
        } else {
            dao = new InMemoryTransactionDao();
            System.out.println("ExpenseTracer starting with in-memory storage.");
            System.out.println("Create db.properties to enable MySQL persistence.");
        }

        TransactionService service = new TransactionServiceImpl(dao);
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Swing's default look and feel will be used.
            }
            new MainFrame(service).setVisible(true);
        });
    }
}
