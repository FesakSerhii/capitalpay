package kz.capitalpay.server.paysystems.systems.halyksoap.repository;

import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykPaymentOrderAcs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HalykPaymentOrderAcsRepository extends JpaRepository<HalykPaymentOrderAcs, Long> {
    HalykPaymentOrderAcs findTopByMd(String MD);
}
