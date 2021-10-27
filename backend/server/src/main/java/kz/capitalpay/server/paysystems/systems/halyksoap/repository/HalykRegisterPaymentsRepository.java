package kz.capitalpay.server.paysystems.systems.halyksoap.repository;

import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.paysystems.systems.halyksoap.repository.projection.RegisterPaymentsStatistic;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HalykRegisterPaymentsRepository extends CrudRepository<Payment, Long> {
    //    @Query(value = "SELECT p.total_amount as totalAmount, " +
//            " p.merchant_id as merchantId, " +
//            " p.currency as currency, " +
//            " p.description as description," +
//            " p.local_date_time as localDateTime " +
//            " FROM Payment p " +
//            " WHERE local_date_time >= :after::date " +
//            "  AND local_date_time <= :before::date + '1 day'::interval) ", nativeQuery = true)
    @Query(value = "SELECT SUM(p.totalAmount) as totalAmount, " +
            " p.merchantId as merchantId, " +
            " p.currency as currency " +
            " FROM Payment p " +
            " WHERE p.timestamp >= ?2 AND p.timestamp <= ?1 " +
            " GROUP BY p.merchantId, p.currency ")
    List<RegisterPaymentsStatistic> findAllByTimestampAfterAndTimestampBeforeAndStatus(Long before, Long after);
}
