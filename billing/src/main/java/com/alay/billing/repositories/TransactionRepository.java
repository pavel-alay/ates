package com.alay.billing.repositories;

import com.alay.billing.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

        @Query(value = "SELECT t FROM Transaction t WHERE t.billingCycle IS NULL AND t.task IS NOT NULL")
        Collection<Transaction> getOpenTaskTransactions();
}
