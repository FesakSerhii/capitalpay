package kz.capitalpay.server.paysystems.systems.halyksoap.repository;

import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.paysystems.systems.halyksoap.repository.projection.RegisterPaymentsStatistic;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HalykRegisterPaymentsRepository extends CrudRepository<Payment, Long> {
    @Query("SELECT p.totalAmount as totalAmount, " +
            " p.merchantId as merchantId, " +
            " p.currency as currency " +
            " FROM Payment p " +
            " WHERE p.timestamp <= ?1 AND p.timestamp >= ?2 ")
    List<RegisterPaymentsStatistic> findAllByTimestampAfterAndTimestampBeforeAndStatus(long before, long after,
                                                                                       String status);
}
