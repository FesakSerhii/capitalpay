package kz.capitalpay.server.registerpayments.repository;

import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.registerpayments.dto.PaymentStatistic;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentStatisticRepository extends CrudRepository<Payment, Long> {
    @Query("SELECT SUM(p.totalAmount) as totalAmount, p.merchantId as merchantId, p.currency as currency FROM Payment p" +
            " WHERE p.status='SUCCESS' AND p.localDateTime>?1 AND p.localDateTime<?2" +
            " GROUP BY p.merchantId, p.currency")
    List<PaymentStatistic> findTopByLocalDateTime(LocalDateTime dateTime, LocalDateTime localDate);
    //name is not make sense findallpaymennByMerchantIdStatusBeforeAfter
}
