package kz.capitalpay.server.paysystems.systems.halyksoap.repository;

import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykPurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HalykPurchaseOrderRepository extends JpaRepository<HalykPurchaseOrder, Long> {
    Optional<HalykPurchaseOrder> findByOrderId(String orderId);
}
