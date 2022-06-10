package kz.capitalpay.server.paysystems.systems.halyksoap.repository;

import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HalykOrderRepository extends JpaRepository<HalykOrder, Long> {

    HalykOrder findTopByMd(String md);

    HalykOrder findTopByPareq(String pareq);

    HalykOrder findTopBySessionid(String sessionid);
}
