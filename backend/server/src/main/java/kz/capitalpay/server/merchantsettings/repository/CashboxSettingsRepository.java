package kz.capitalpay.server.merchantsettings.repository;

import kz.capitalpay.server.merchantsettings.model.CashboxSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashboxSettingsRepository extends JpaRepository<CashboxSettings, Long> {
    CashboxSettings findTopByFieldNameAndCashboxId(String fieldName, Long cashboxId);
}
