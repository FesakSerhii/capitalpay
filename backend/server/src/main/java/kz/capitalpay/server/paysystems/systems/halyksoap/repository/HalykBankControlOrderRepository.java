package kz.capitalpay.server.paysystems.systems.halyksoap.repository;

import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykBankControlOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HalykBankControlOrderRepository extends JpaRepository<HalykBankControlOrder, Long> {
    Optional<HalykBankControlOrder> findByOrderId(String orderId);
}
