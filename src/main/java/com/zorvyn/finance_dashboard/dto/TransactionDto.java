package com.zorvyn.finance_dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.zorvyn.finance_dashboard.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

	private Long id;
	private BigDecimal amount;
	private TransactionType type;
	private String category;
	private LocalDate date;
	private String description;
	private String createdBy;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
