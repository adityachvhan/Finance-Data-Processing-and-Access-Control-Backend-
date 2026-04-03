package com.zorvyn.finance_dashboard.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.zorvyn.finance_dashboard.dto.DashboardSummaryDto;
import com.zorvyn.finance_dashboard.dto.TransactionDto;
import com.zorvyn.finance_dashboard.entity.Transaction;
import com.zorvyn.finance_dashboard.entity.User;
import com.zorvyn.finance_dashboard.enums.TransactionType;
import com.zorvyn.finance_dashboard.execption.ResourceNotFoundException;
import com.zorvyn.finance_dashboard.repository.TransactionRepository;
import com.zorvyn.finance_dashboard.repository.UserRepository;
import com.zorvyn.finance_dashboard.request.TransactionCreateRequest;
import com.zorvyn.finance_dashboard.request.TransactionUpdateRequest;

@Service
public class TransactionServiceImpl implements TransactionService {

	private TransactionRepository transactionRepository;

	private UserRepository userRepository;

	public TransactionServiceImpl(TransactionRepository transactionRepository, UserRepository userRepository) {
		this.transactionRepository = transactionRepository;
		this.userRepository = userRepository;
	}

	@Override
	public Transaction findById(Long id) throws ResourceNotFoundException {

		return transactionRepository.findByIdAndDeletedFalse(id)
				.orElseThrow(() -> new ResourceNotFoundException("Transaction", id));
	}

	@Override
	public List<TransactionDto> getAllTransactions() {

		List<Transaction> transactions = transactionRepository.findByDeletedFalse();
		List<TransactionDto> responseList = new ArrayList<>();

		for (Transaction t : transactions) {
			responseList.add(toResponse(t));
		}

		return responseList;
	}

	@Override
	public Page<TransactionDto> getAllTransactionsPaged(Pageable pageable) {

		return transactionRepository.findByDeletedFalse(pageable).map(this::toResponse);
	}

	@Override
	public TransactionDto getTransactionById(Long id) throws ResourceNotFoundException {
		return toResponse(findById(id));
	}

	// create new tarnsactions
	@Override
	public TransactionDto create(TransactionCreateRequest createRequest, User user) {

		Transaction transaction = new Transaction();
		transaction.setAmount(createRequest.getAmount());
		transaction.setType(createRequest.getType());
		transaction.setCategory(createRequest.getCategory());
		transaction.setDescription(createRequest.getDescription());
		transaction.setDate(createRequest.getDate());
		transaction.setCreatedBy(user);

		return toResponse(transactionRepository.save(transaction));
	}

	@Override
	public TransactionDto update(TransactionUpdateRequest updateRequest, Long id) throws ResourceNotFoundException {

		Transaction transaction = findById(id);

		if (updateRequest.getAmount() != null) {
			transaction.setAmount(updateRequest.getAmount());
		}
		if (updateRequest.getType() != null) {
			transaction.setType(updateRequest.getType());
		}
		if (updateRequest.getCategory() != null) {
			transaction.setCategory(updateRequest.getCategory());
		}
		if (updateRequest.getDate() != null) {
			transaction.setDate(updateRequest.getDate());
		}
		if (updateRequest.getDescription() != null) {
			transaction.setDescription(updateRequest.getDescription());
		}
		return toResponse(transactionRepository.save(transaction));
	}

	@Override
	public void delete(Long id) throws ResourceNotFoundException {

		Transaction transaction = findById(id);
		transaction.setDeleted(true);
		transactionRepository.save(transaction);
	}

	@Override
	public TransactionDto toResponse(Transaction transaction) {

		return new TransactionDto(

				transaction.getId(), transaction.getAmount(), transaction.getType(), transaction.getCategory(),
				transaction.getDate(), transaction.getDescription(), transaction.getCreatedBy().getUsername(),
				transaction.getCreatedAt(), transaction.getUpdatedAt()

		);
	}

	@Override
	public List<TransactionDto> filterByType(TransactionType type) {
		return transactionRepository.findByTypeAndDeletedFalse(type).stream().map(this::toResponse)
				.collect(Collectors.toList());
	}

	@Override
	public List<TransactionDto> filterByCategory(String category) {
		return transactionRepository.findByCategoryContainingIgnoreCaseAndDeletedFalse(category).stream()
				.map(this::toResponse).collect(Collectors.toList());
	}

	@Override
	public List<TransactionDto> filterByDateRange(LocalDate startDate, LocalDate endDate) {
		return transactionRepository.findByDateBetweenAndDeletedFalse(startDate, endDate).stream().map(this::toResponse)
				.collect(Collectors.toList());
	}

	@Override
	public List<TransactionDto> getRecentTransactions() {
		return transactionRepository.findTop5ByDeletedFalseOrderByCreatedAtDesc().stream().map(this::toResponse)
				.collect(Collectors.toList());
	}

	@Override
	public DashboardSummaryDto getDashboardSummary() {
		BigDecimal totalIncome = transactionRepository.sumByType(TransactionType.INCOME);
		BigDecimal totalExpenses = transactionRepository.sumByType(TransactionType.EXPENSE);
		BigDecimal netBalance = totalIncome.subtract(totalExpenses);

		// Category totals (all types combined)
		Map<String, BigDecimal> categoryTotals = new LinkedHashMap<>();
		for (Object[] row : transactionRepository.sumByCategoryAndType(TransactionType.INCOME)) {
			categoryTotals.merge((String) row[0], (BigDecimal) row[1], BigDecimal::add);
		}
		for (Object[] row : transactionRepository.sumByCategoryAndType(TransactionType.EXPENSE)) {
			categoryTotals.merge((String) row[0], (BigDecimal) row[1], BigDecimal::add);
		}

		// Monthly breakdowns
		Map<String, BigDecimal> monthlyIncome = new LinkedHashMap<>();
		for (Object[] row : transactionRepository.monthlyTotalsByType(TransactionType.INCOME)) {
			monthlyIncome.put((String) row[0], (BigDecimal) row[1]);
		}
		Map<String, BigDecimal> monthlyExpenses = new LinkedHashMap<>();
		for (Object[] row : transactionRepository.monthlyTotalsByType(TransactionType.EXPENSE)) {
			monthlyExpenses.put((String) row[0], (BigDecimal) row[1]);
		}

		List<TransactionDto> recent = getRecentTransactions();

		return DashboardSummaryDto.builder().totalIncome(totalIncome).totalExpenses(totalExpenses)
				.netBalance(netBalance).categoryTotals(categoryTotals).recentTransactions(recent)
				.monthlyIncome(monthlyIncome).monthlyExpenses(monthlyExpenses).build();

	}

}
