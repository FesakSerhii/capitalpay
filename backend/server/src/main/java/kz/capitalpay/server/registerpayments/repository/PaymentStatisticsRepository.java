package kz.capitalpay.server.registerpayments.repository;

import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.registerpayments.dto.PaymentStatistic;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentStatisticsRepository extends CrudRepository<Payment, Long> {
    @Query("SELECT SUM(totalAmount) as total_amount, merchantId, currency FROM Payment " +
            " WHERE status='SUCCESS' AND localDateTime>?1 AND localDateTime<?2" +
            " GROUP BY merchantId, currency")
    List<PaymentStatistic> findTopByLocalDateTime(LocalDateTime dateTime, LocalDateTime localDate);

}
