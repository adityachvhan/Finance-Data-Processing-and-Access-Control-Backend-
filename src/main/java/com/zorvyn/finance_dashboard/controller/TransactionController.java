package com.zorvyn.finance_dashboard.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zorvyn.finance_dashboard.dto.DashboardSummaryDto;
import com.zorvyn.finance_dashboard.dto.TransactionDto;
import com.zorvyn.finance_dashboard.entity.User;
import com.zorvyn.finance_dashboard.enums.TransactionType;
import com.zorvyn.finance_dashboard.execption.ResourceNotFoundException;
import com.zorvyn.finance_dashboard.repository.UserRepository;
import com.zorvyn.finance_dashboard.request.TransactionCreateRequest;
import com.zorvyn.finance_dashboard.request.TransactionUpdateRequest;
import com.zorvyn.finance_dashboard.response.ApiResponse;
import com.zorvyn.finance_dashboard.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

	private TransactionService transactionService;
	private UserRepository userRepository;

	public TransactionController(TransactionService transactionService, UserRepository userRepository) {
		this.transactionService = transactionService;
		this.userRepository = userRepository;
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
	public ResponseEntity<TransactionDto> getTransactionById(@PathVariable Long id) throws ResourceNotFoundException {

		return ResponseEntity.ok(transactionService.getTransactionById(id));
	}

	@PostMapping("/create")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<TransactionDto> createTransaction(@RequestBody TransactionCreateRequest request,
			@AuthenticationPrincipal String email ) {

		User user = userRepository.findByEmail(email);

		TransactionDto created = transactionService.create(request, user);

		return new ResponseEntity<>(created, HttpStatus.CREATED);
	}

	@PostMapping("/update/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<TransactionDto> updateTransaction(@Valid @RequestBody TransactionUpdateRequest updateRequest,
			@PathVariable Long id) throws ResourceNotFoundException {

		return ResponseEntity.ok(transactionService.update(updateRequest, id));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse> deleteTransaction(@PathVariable Long id) throws ResourceNotFoundException {
		transactionService.delete(id);
		return ResponseEntity.ok(new ApiResponse(true, "Transaction deleted successfully"));
	}

	// VIEWER, ANALYST, ADMIN can view
	@GetMapping
	@PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
	public ResponseEntity<List<TransactionDto>> getAllTransactions(@RequestParam(required = false) TransactionType type,
			@RequestParam(required = false) String category,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

		List<TransactionDto> result;

		if (type != null && startDate != null && endDate != null) {
			result = transactionService.filterByDateRange(startDate, endDate).stream().filter(t -> t.getType() == type)
					.toList();
		} else if (type != null) {
			result = transactionService.filterByType(type);
		} else if (category != null) {
			result = transactionService.filterByCategory(category);
		} else if (startDate != null && endDate != null) {
			result = transactionService.filterByDateRange(startDate, endDate);
		} else {
			result = transactionService.getAllTransactions();
		}
		return ResponseEntity.ok(result);
	}

	@GetMapping("/paged")
	@PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
	public ResponseEntity<Page<TransactionDto>> getTransactionsPaged(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sortBy,
			@RequestParam(defaultValue = "desc") String direction) {

		Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(page, size, sort);
		return ResponseEntity.ok(transactionService.getAllTransactionsPaged(pageable));
	}

	@GetMapping("/recent")
	@PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
	public ResponseEntity<List<TransactionDto>> getRecentTransactions() {
		return ResponseEntity.ok(transactionService.getRecentTransactions());
	}

	// ANALYST and ADMIN can view dashboard summary
	@GetMapping("/dashboard/summary")
	@PreAuthorize("hasAnyRole('ANALYST','ADMIN')")
	public ResponseEntity<DashboardSummaryDto> getDashboardSummary() {
		return ResponseEntity.ok(transactionService.getDashboardSummary());
	}
}
