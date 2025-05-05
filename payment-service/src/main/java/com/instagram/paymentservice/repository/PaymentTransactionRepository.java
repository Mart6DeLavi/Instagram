package com.instagram.paymentservice.repository;

import com.instagram.paymentservice.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    @Query("""
        SELECT p
        FROM PaymentTransaction p
        WHERE
        p.paymentIntentId = :paymentIntentId
    """)
    Optional<PaymentTransaction> findByPaymentIntentId(@Param("paymentIntentId") String paymentIntentId);
}
