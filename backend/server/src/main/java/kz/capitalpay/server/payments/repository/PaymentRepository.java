package kz.capitalpay.server.payments.repository;

import kz.capitalpay.server.payments.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    List<Payment> findByCashboxIdAndBillId(Long cashboxId, String billId);

    Payment findTopByCashboxIdAndBillId(Long cashboxId, String billId);

    Payment findByGuid(String guid);

    Payment findTopByBillId(String billId);

    Payment findTopByPaySysPayId(String paySysPayId);

    List<Payment> findByMerchantId(Long merchantId);

    List<Payment> findByCashboxIdAndStatus(Long cashboxId, String status);

    @Query(value = "select * " +
            " from payment " +
            " order by timestamp desc " +
            " limit 1 ", nativeQuery = true)
    Optional<Payment> findLast();

    Optional<Payment> findByPaySysPayId(String orderId);
}
