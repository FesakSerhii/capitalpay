package kz.capitalpay.server.payments.repository;

import kz.capitalpay.server.payments.model.P2pPaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface P2pPaymentLogRepository extends JpaRepository<P2pPaymentLog, Long> {
}
