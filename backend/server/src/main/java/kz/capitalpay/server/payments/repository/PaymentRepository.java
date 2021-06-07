package kz.capitalpay.server.payments.repository;

import kz.capitalpay.server.payments.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment,String> {
    List<Payment> findByCashboxIdAndAndBillId(Long cashboxId,String billId);
    Payment findTopByCashboxIdAndAndBillId(Long cashboxId,String billId);
    Payment findByGuid(String guid);
    Payment findTopByBillId(String billId);
    Payment findTopByPaySysPayId(String paySysPayId);
    List<Payment> findByMerchantId(Long merchantId);
    List<Payment> findByCashboxIdAndStatus(Long cashboxId, String status);
    @Query("SELECT SUM(totalAmount) as totalAmount, merchantId, currency FROM Payment " +
            " WHERE status='SUCCESS' AND localDateTime>?1 AND localDateTime<?2" +
            " GROUP BY merchantId, currency")
    List<Payment> findTopByLocalDateTime(LocalDateTime dateTime, LocalDateTime localDate);
}
