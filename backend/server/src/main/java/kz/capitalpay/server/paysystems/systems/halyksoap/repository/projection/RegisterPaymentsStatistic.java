package kz.capitalpay.server.paysystems.systems.halyksoap.repository.projection;

import java.math.BigDecimal;

public interface RegisterPaymentsStatistic {
    String getMerchantId();
    BigDecimal getTotalAmount();
    String getCurrency();
    Long getCashboxId();
}
