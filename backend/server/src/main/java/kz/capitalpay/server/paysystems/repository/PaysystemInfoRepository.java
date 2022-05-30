package kz.capitalpay.server.paysystems.repository;

import kz.capitalpay.server.paysystems.model.PaysystemInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaysystemInfoRepository extends JpaRepository<PaysystemInfo, Long> {
    PaysystemInfo findTopByComponentName(String componentName);
}
