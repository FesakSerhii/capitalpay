package kz.capitalpay.server.merchantsettings.repository;

import kz.capitalpay.server.merchantsettings.model.MerchantSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantSettingsRepository extends JpaRepository<MerchantSettings, Long> {

    MerchantSettings findTopByFieldNameAndMerchantId(String fieldName, Long merchantId);
}
