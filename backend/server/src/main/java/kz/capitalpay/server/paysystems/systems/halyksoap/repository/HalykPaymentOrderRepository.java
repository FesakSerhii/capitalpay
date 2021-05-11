package kz.capitalpay.server.paysystems.systems.halyksoap.repository;

import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykPaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HalykPaymentOrderRepository extends JpaRepository<HalykPaymentOrder,Long> {
    HalykPaymentOrder findTopByMd(String md);
}
