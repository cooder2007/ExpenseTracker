package com.team.expense.service;

import java.time.YearMonth;
import java.util.Map;

public interface ReportService {
    double totalIncome(YearMonth month);
    double totalExpense(YearMonth month);
    Map<String, Double> categoryTotals(YearMonth month);
}
