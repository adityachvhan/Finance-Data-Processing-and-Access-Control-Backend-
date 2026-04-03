package com.zorvyn.finance_dashboard.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDto {

	private BigDecimal totalIncome;
	private BigDecimal totalExpenses;
	private BigDecimal netBalance;
	private Map<String, BigDecimal> categoryTotals;
	private List<TransactionDto> recentTransactions;
	private Map<String, BigDecimal> monthlyIncome;
	private Map<String, BigDecimal> monthlyExpenses;
}
