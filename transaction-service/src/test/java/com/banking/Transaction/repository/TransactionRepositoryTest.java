
package com.banking.Transaction.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.banking.Transaction.entity.Transaction;
import com.banking.Transaction.enums.TransactionType;
import com.banking.Transaction.enums.TransactionStatus;



@DataJpaTest
class TransactionRepositoryTest {

	@Autowired
	private TransactionRepository transactionRepository;

	private Transaction transaction;

	@BeforeEach
	void setUp() {
		transactionRepository.deleteAll();

		transaction = Transaction.builder()
		        .accountNumber("12345678901")
		        .amount(500.0)
		        .transactionType(TransactionType.DEPOSIT)
		        .status(TransactionStatus.SUCCESS)
		        .transactionDate(LocalDateTime.now())
		        .build();
	}
		
	// 1. Save transaction
	@Test
	void saveTransaction_shouldSaveSuccessfully() {
		Transaction saved = transactionRepository.save(transaction);

		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getAccountNumber()).isEqualTo("12345678901");
		assertThat(saved.getAmount()).isEqualTo(500.0);
	}

	// 2. Find transaction by ID
	@Test
	void findById_whenTransactionExists_shouldReturnTransaction() {
		Transaction saved = transactionRepository.save(transaction);

		Optional<Transaction> result =
				transactionRepository.findById(saved.getId());

		assertThat(result).isPresent();
		assertThat(result.get().getAccountNumber())
		.isEqualTo("12345678901");
	}

	// 3. Find transaction by ID when it doesn't exist
	@Test
	void findById_whenTransactionDoesNotExist_shouldReturnEmpty() {
		Optional<Transaction> result =
				transactionRepository.findById(999L);

		assertThat(result).isEmpty();
	}

	// 4. Find all transactions
	@Test
	void findAll_shouldReturnAllTransactions() {
		transactionRepository.save(transaction);
		Transaction second = Transaction.builder()
		        .accountNumber("12345678902")
		        .amount(1000.0)
		        .transactionType(TransactionType.DEPOSIT)
		        .status(TransactionStatus.SUCCESS)
		        .transactionDate(LocalDateTime.now())
		        .build();

		transactionRepository.save(second);

		List<Transaction> result = transactionRepository.findAll();

		assertThat(result).hasSize(2);
	}

	// 5. Update transaction
	@Test
	void save_whenTransactionIsUpdated_shouldPersistChanges() {
		Transaction saved = transactionRepository.save(transaction);

		saved.setAmount(750.0);
		transactionRepository.save(saved);

		Optional<Transaction> result =
				transactionRepository.findById(saved.getId());

		assertThat(result).isPresent();
		assertThat(result.get().getAmount()).isEqualTo(750.0);
	}

	// 6. Delete transaction
	@Test
	void deleteById_shouldRemoveTransaction() {
		Transaction saved = transactionRepository.save(transaction);

		transactionRepository.deleteById(saved.getId());

		assertThat(transactionRepository.findById(saved.getId()))
		.isEmpty();
	}

	// 7. Count transactions
	@Test
	void count_shouldReturnNumberOfTransactions() {
		transactionRepository.save(transaction);

		assertThat(transactionRepository.count()).isEqualTo(1);
	}

	// 8. Check whether transaction exists
	@Test
	void existsById_whenTransactionExists_shouldReturnTrue() {
		Transaction saved = transactionRepository.save(transaction);

		assertThat(transactionRepository.existsById(saved.getId()))
		.isTrue();
	}

	// 9. Delete all transactions
	@Test
	void deleteAll_shouldRemoveAllTransactions() {
		transactionRepository.save(transaction);

		transactionRepository.deleteAll();

		assertThat(transactionRepository.findAll()).isEmpty();
	}


	@Test
	void findByAccountNumber_shouldReturnMatchingTransactions() {
		transactionRepository.save(transaction);

		List<Transaction> result =
				transactionRepository.findByAccountNumber("12345678901");

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getAccountNumber())
		.isEqualTo("12345678901");
	}

}
