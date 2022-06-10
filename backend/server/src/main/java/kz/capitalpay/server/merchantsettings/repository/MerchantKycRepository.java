package kz.capitalpay.server.merchantsettings.repository;

import kz.capitalpay.server.merchantsettings.model.MerchantKyc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantKycRepository extends JpaRepository<MerchantKyc, Long> {

    MerchantKyc findTopByFieldNameAndMerchantId(String fieldName, Long merchantId);
}
