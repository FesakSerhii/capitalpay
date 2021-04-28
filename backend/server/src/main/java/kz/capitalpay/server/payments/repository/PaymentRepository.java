package kz.capitalpay.server.payments.repository;

import kz.capitalpay.server.payments.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment,String> {
    List<Payment> findByCashboxIdAndAndBillId(Long cashboxId,String billId);
    Payment findByGuid(String guid);
    Payment findTopByBillId(String billId);
}
