package kz.capitalpay.server.paysystems.systems.halyksoap.repository;

import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.paysystems.systems.halyksoap.dto.PaymentStatistic;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentStatisticRepository extends CrudRepository<Payment, Long> {
    @Query("SELECT SUM(p.totalAmount) as totalAmount, " +
            " p.merchantId as merchantId, " +
            " p.currency as currency, " +
            " p.cashboxId as cashboxId " +
            " FROM Payment p" +
            " WHERE p.status='SUCCESS' AND p.localDateTime>?1 AND p.localDateTime<?2" +
            " GROUP BY p.merchantId, p.currency, p.cashboxId")
    List<PaymentStatistic> findAll(LocalDateTime dateTime, LocalDateTime localDate);
    //TODO:should fix name method
}
