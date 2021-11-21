package com.alay.billing.repositories;

import com.alay.billing.entities.BillingCycle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingCycleRepository extends JpaRepository<BillingCycle, Long> {
}
