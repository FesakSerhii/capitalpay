package kz.capitalpay.server.merchantsettings.dto;

import javax.validation.constraints.NotNull;
import java.util.List;

public class MerchantKycDTO {
    @NotNull
    Long merchantId;
    List<MerchantKycFieldDTO> fields;

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public List<MerchantKycFieldDTO> getFields() {
        return fields;
    }

    public void setFields(List<MerchantKycFieldDTO> fields) {
        this.fields = fields;
    }
}
