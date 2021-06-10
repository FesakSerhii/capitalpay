package kz.capitalpay.server.paysystems.systems.halyksoap.repository;

import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.paysystems.systems.halyksoap.repository.projection.RegisterPaymentsStatistic;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HalykRegisterPaymentsRepository extends CrudRepository<Payment, Long> {
    @Query("SELECT SUM(p.totalAmount) as totalAmount, " +
            " p.merchantId as merchantId, " +
            " p.currency as currency, " +
            " p.cashboxId as cashboxId " +
            " FROM Payment p" +
            " GROUP BY p.merchantId, p.currency, p.cashboxId")
    List<RegisterPaymentsStatistic> findAllByTimestampAfterAndTimestampBeforeAndStatus(long before, long after,
                                                                                       String status);
}
