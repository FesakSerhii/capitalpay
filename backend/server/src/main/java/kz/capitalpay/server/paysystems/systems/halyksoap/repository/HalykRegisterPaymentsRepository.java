package kz.capitalpay.server.paysystems.systems.halyksoap.repository;

import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.paysystems.systems.halyksoap.repository.projection.RegisterPaymentsStatistic;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HalykRegisterPaymentsRepository extends CrudRepository<Payment, Long> {
    @Query(value = "SELECT p.total_amount as totalAmount, " +
            " p.merchant_id as merchantId, " +
            " p.currency as currency, " +
            " p.description as description," +
            " p.local_date_time as localDateTime " +
            " FROM Payment p " +
            " WHERE local_date_time >= ?1::date " +
            "  AND local_date_time <= (?2::date + '1 day'::interval) ", nativeQuery = true)
    List<RegisterPaymentsStatistic> findAllByTimestampAfterAndTimestampBeforeAndStatus(String before, String after,
                                                                                       String status);
}
