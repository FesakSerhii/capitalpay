package kz.capitalpay.server.payments.repository;

import kz.capitalpay.server.payments.model.CheckCardValidityPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CheckCardValidityPaymentRepository extends JpaRepository<CheckCardValidityPayment, Long> {

    @Query(value = "select * " +
            " from check_card_validity_payment " +
            " order by id desc " +
            " limit 1 ", nativeQuery = true)
    Optional<CheckCardValidityPayment> findLast();
}
