package com.zorvyn.finance_dashboard.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.zorvyn.finance_dashboard.dto.DashboardSummaryDto;
import com.zorvyn.finance_dashboard.dto.TransactionDto;
import com.zorvyn.finance_dashboard.entity.Transaction;
import com.zorvyn.finance_dashboard.entity.User;
import com.zorvyn.finance_dashboard.enums.TransactionType;
import com.zorvyn.finance_dashboard.execption.ResourceNotFoundException;
import com.zorvyn.finance_dashboard.request.TransactionCreateRequest;
import com.zorvyn.finance_dashboard.request.TransactionUpdateRequest;

public interface TransactionService {

	Transaction findById(Long id) throws ResourceNotFoundException;

    List<TransactionDto> getAllTransactions();

    Page<TransactionDto> getAllTransactionsPaged(Pageable pageable);

    TransactionDto getTransactionById(Long id) throws ResourceNotFoundException;

    TransactionDto create(TransactionCreateRequest createRequest, User user);

    TransactionDto update(TransactionUpdateRequest updateRequest, Long id) throws ResourceNotFoundException;

    void delete(Long id) throws ResourceNotFoundException;

    List<TransactionDto> filterByType(TransactionType type);

    List<TransactionDto> filterByCategory(String category);

    List<TransactionDto> filterByDateRange(LocalDate startDate, LocalDate endDate);

    List<TransactionDto> getRecentTransactions();

    DashboardSummaryDto getDashboardSummary();

    TransactionDto toResponse(Transaction transaction);
}
