package kz.capitalpay.server.paysystems.systems.halyksoap.repository;

import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykSavedCardsP2pOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HalykSavedCardsP2pOrderRepository extends JpaRepository<HalykSavedCardsP2pOrder, Long> {

    Optional<HalykSavedCardsP2pOrder> findByOrderId(String orderId);
}
