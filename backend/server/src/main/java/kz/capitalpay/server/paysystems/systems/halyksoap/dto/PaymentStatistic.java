package kz.capitalpay.server.paysystems.systems.halyksoap.dto;

import java.math.BigDecimal;

public interface PaymentStatistic {
    String getMerchantId();
    BigDecimal getTotalAmount();
    String getCurrency();
    Long getCashboxId();
}
