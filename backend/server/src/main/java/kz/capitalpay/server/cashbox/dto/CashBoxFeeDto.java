package kz.capitalpay.server.cashbox.dto;

import javax.validation.constraints.NotNull;
import java.util.List;

public class CashBoxFeeDto {
    @NotNull
    private long merchantId;
    private List<CashBoxFeeListDto> feeList;

    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
    }

    public List<CashBoxFeeListDto> getFeeList() {
        return feeList;
    }

    public void setFeeList(List<CashBoxFeeListDto> feeList) {
        this.feeList = feeList;
    }
}
