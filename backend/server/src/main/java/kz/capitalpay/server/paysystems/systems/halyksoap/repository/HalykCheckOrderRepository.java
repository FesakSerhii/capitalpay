package kz.capitalpay.server.paysystems.systems.halyksoap.repository;

import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykCheckOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HalykCheckOrderRepository extends JpaRepository<HalykCheckOrder,Long> {
}
