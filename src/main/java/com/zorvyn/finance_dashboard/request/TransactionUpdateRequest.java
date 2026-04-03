package com.zorvyn.finance_dashboard.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.zorvyn.finance_dashboard.enums.TransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class TransactionUpdateRequest {

	@DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 13, fraction = 2, message = "Invalid amount format")
    private BigDecimal amount;

    private TransactionType type;

    @Size(max = 100)
    private String category;

    private LocalDate date;

    @Size(max = 500)
    private String description;
}
