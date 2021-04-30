package kz.capitalpay.server.paysystems.systems.halykbank.repository;

import kz.capitalpay.server.paysystems.systems.halykbank.model.HalykPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HalykPaymentRepository extends JpaRepository<HalykPayment, Long> {
    HalykPayment findTopByHalykId(String halykId);
}
