package kz.capitalpay.server.paysystems.dto;

import kz.capitalpay.server.paysystems.model.Paysystem;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class MerchantEditListDTO {

    @NotNull
    Long merchantId;

    @NotEmpty
    List<Long> paysystemList;

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public List<Long> getPaysystemList() {
        return paysystemList;
    }

    public void setPaysystemList(List<Long> paysystemList) {
        this.paysystemList = paysystemList;
    }
}
