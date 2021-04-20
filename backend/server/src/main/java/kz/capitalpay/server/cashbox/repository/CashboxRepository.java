package kz.capitalpay.server.cashbox.repository;

import kz.capitalpay.server.cashbox.model.Cashbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CashboxRepository extends JpaRepository<Cashbox,Long> {

    List<Cashbox> findByMerchantIdAndDeleted(Long merchantId, boolean deleted);

}
