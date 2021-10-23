package kz.capitalpay.server.paysystems.systems.halyksoap.repository;

import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.paysystems.systems.halyksoap.repository.projection.RegisterPaymentsStatistic;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HalykRegisterPaymentsRepository extends CrudRepository<Payment, Long> {
    @Query(value = "SELECT p.totalAmount as totalAmount, " +
            " p.merchantId as merchantId, " +
            " p.currency as currency, " +
            " p.description as description," +
            " p.localDateTime as localDateTime " +
            " FROM Payment p " +
            " WHERE local_date_time >= to_date(?2, 'YYYY-MM-DD') " +
            "  AND local_date_time <= to_date(?1, 'YYYY-MM-DD') + '1 day'::interval) ")
    List<RegisterPaymentsStatistic> findAllByTimestampAfterAndTimestampBeforeAndStatus(String before, String after,
                                                                                       String status);
}
