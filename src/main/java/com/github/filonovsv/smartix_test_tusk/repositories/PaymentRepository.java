package com.github.filonovsv.smartix_test_tusk.repositories;

import com.github.filonovsv.smartix_test_tusk.models.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long>, PagingAndSortingRepository<Payment, Long> {
    Page<Payment> findByPayer(String payer, PageRequest pageRequest);
}
