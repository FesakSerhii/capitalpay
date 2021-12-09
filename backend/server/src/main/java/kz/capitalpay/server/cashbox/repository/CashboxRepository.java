package kz.capitalpay.server.cashbox.repository;

import kz.capitalpay.server.cashbox.model.Cashbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CashboxRepository extends JpaRepository<Cashbox, Long> {

    List<Cashbox> findByMerchantIdAndDeleted(Long merchantId, boolean deleted);

    List<Cashbox> findByDeleted(boolean deleted);

    @Query(nativeQuery = true, value = " select user_card_id from cashbox where id = ?1 ")
    Long findCardById(Long id);
}
