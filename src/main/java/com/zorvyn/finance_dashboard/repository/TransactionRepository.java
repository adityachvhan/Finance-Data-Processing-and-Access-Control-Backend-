package com.zorvyn.finance_dashboard.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zorvyn.finance_dashboard.entity.Transaction;
import com.zorvyn.finance_dashboard.enums.TransactionType;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	Optional<Transaction> findByIdAndDeletedFalse(Long id);

	List<Transaction> findByDeletedFalse();

	List<Transaction> findByTypeAndDeletedFalse(TransactionType type);

	List<Transaction> findByCategoryContainingIgnoreCaseAndDeletedFalse(String category);

	List<Transaction> findByDateBetweenAndDeletedFalse(LocalDate startDate, LocalDate endDate);

	Page<Transaction> findByDeletedFalse(Pageable pageable);

	List<Transaction> findTop5ByDeletedFalseOrderByCreatedAtDesc();

	@Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = :type AND t.deleted = false")
	BigDecimal sumByType(@Param("type") TransactionType type);

	@Query("SELECT t.category, SUM(t.amount) FROM Transaction t WHERE t.type = :type AND t.deleted = false GROUP BY t.category")
	List<Object[]> sumByCategoryAndType(@Param("type") TransactionType type);

	@Query("SELECT FUNCTION('DATE_FORMAT', t.date, '%Y-%m'), SUM(t.amount) FROM Transaction t "
			+ "WHERE t.type = :type AND t.deleted = false GROUP BY FUNCTION('DATE_FORMAT', t.date, '%Y-%m') ORDER BY 1")
	List<Object[]> monthlyTotalsByType(@Param("type") TransactionType type);

	List<Transaction> findByTypeAndDateBetweenAndDeletedFalse(TransactionType type, LocalDate startDate,
			LocalDate endDate);
}
