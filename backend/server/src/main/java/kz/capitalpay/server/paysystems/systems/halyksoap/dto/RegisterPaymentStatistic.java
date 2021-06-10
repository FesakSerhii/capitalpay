package kz.capitalpay.server.paysystems.systems.halyksoap.dto;

import java.math.BigDecimal;

public interface RegisterPaymentStatistic {
    String getMerchantId();
    BigDecimal getTotalAmount();
    String getCurrency();
    Long getCashboxId();
}
