package com.zorvyn.finance_dashboard.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.zorvyn.finance_dashboard.enums.TransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TransactionCreateRequest {

	@NotNull(message = "Amount is required")
	@DecimalMin(value = "0.01", message = "Amount must be greater than 0")
	@Digits(integer = 13, fraction = 2, message = "Invalid amount format")
	private BigDecimal amount;

	@NotNull(message = "Transaction type is required")
	private TransactionType type;

	@NotBlank(message = "Category is required")
	@Size(max = 100, message = "Category cannot exceed 100 characters")
	private String category;

	@NotNull(message = "Date is required")
	private LocalDate date;

	@Size(max = 500, message = "Description cannot exceed 500 characters")
	private String description;
}
