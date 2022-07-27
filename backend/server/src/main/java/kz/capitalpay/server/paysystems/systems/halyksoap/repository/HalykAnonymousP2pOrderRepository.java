package kz.capitalpay.server.paysystems.systems.halyksoap.repository;

import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykAnonymousP2pOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HalykAnonymousP2pOrderRepository extends JpaRepository<HalykAnonymousP2pOrder, Long> {

    Optional<HalykAnonymousP2pOrder> findByOrderId(String orderId);
}
