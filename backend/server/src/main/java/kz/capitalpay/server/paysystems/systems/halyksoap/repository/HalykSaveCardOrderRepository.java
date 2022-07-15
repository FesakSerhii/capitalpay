package kz.capitalpay.server.paysystems.systems.halyksoap.repository;

import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykSaveCardOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HalykSaveCardOrderRepository extends JpaRepository<HalykSaveCardOrder, Long> {

    Optional<HalykSaveCardOrder> findByOrderId(String orderId);
}
