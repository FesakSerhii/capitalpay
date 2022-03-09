package kz.capitalpay.server.paysystems.systems.halyksoap.repository;

import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykTransferOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HalykTransferOrderRepository extends JpaRepository<HalykTransferOrder, Long> {
    HalykTransferOrder findTopByMd(String md);

    HalykTransferOrder findTopByPareq(String pareq);

    HalykTransferOrder findTopBySessionid(String sessionid);
}
