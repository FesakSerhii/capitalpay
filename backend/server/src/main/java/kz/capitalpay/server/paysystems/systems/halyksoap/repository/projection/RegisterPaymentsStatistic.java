package kz.capitalpay.server.paysystems.systems.halyksoap.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface RegisterPaymentsStatistic {
    String getMerchantId();

    BigDecimal getTotalAmount();

    String getCurrency();
    String getDescription();
    LocalDateTime getLocalDateTime();
}
