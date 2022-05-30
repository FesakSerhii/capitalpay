package kz.capitalpay.server.payments.repository;

import kz.capitalpay.server.payments.model.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentLogRepository extends JpaRepository<PaymentLog, Long> {
}
