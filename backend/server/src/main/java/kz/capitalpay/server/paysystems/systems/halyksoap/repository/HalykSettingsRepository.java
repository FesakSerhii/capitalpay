package kz.capitalpay.server.paysystems.systems.halyksoap.repository;

import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HalykSettingsRepository extends JpaRepository<HalykSettings, Long> {
    HalykSettings findTopByFieldNameAndId(String fieldName, Long adminId);
}
