package kz.capitalpay.server.payments.repository;

import kz.capitalpay.server.payments.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    List<Payment> findByCashboxIdAndBillIdAndStatus(Long cashboxId, String billId, String status);

    Payment findTopByCashboxIdAndBillId(Long cashboxId, String billId);

    Payment findByGuid(String guid);

    Payment findTopByBillId(String billId);

    Payment findTopByPaySysPayId(String paySysPayId);

    List<Payment> findAllByMerchantIdAndSaveBankCardFalse(Long merchantId);

    List<Payment> findAllBySaveBankCardFalse();

    List<Payment> findByCashboxIdAndStatusAndOutgoingFalse(Long cashboxId, String status);

    List<Payment> findByCashboxIdAndStatusAndOutgoingTrue(Long cashboxId, String status);

    @Query(value = "select * " +
            " from payment " +
            " order by pay_sys_pay_id desc " +
            " limit 1 ", nativeQuery = true)
    Optional<Payment> findLastByPaySysPayId();

    Optional<Payment> findByPaySysPayId(String orderId);

    Page<Payment> findAll(Specification<Payment> specification, Pageable pageable);
}
