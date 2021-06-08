package kz.capitalpay.server.paysystems.systems.halyksoap.repository;

import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.paysystems.systems.halyksoap.dto.PaymentStatistic;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface HalykPaymentStatisticRepository extends CrudRepository<Payment, Long> {
    @Query("SELECT SUM(p.totalAmount) as totalAmount, " +
            " p.merchantId as merchantId, " +
            " p.currency as currency, " +
            " p.cashboxId as cashboxId " +
            " FROM Payment p" +
            " GROUP BY p.merchantId, p.currency, p.cashboxId")
    List<PaymentStatistic> findAllByLocalDateTimeIsBeforeAndLocalDateTimeIsAfterAndStatus(LocalDateTime before,
                                                                                          LocalDateTime after, String status);
}
