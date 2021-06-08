package kz.capitalpay.server.registerpayments.dto;

import java.math.BigDecimal;

public interface PaymentStatistic {
    String getMerchantId();
    BigDecimal getTotalAmount();
    String getCurrency();
}
