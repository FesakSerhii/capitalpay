package kz.capitalpay.server.cashbox.repository;

import kz.capitalpay.server.cashbox.dto.CashBoxFeeDto;
import kz.capitalpay.server.cashbox.model.Cashbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Repository
public interface CashboxRepository extends JpaRepository<Cashbox, Long> {

    List<Cashbox> findByMerchantIdAndDeletedFalse(Long merchantId);

    List<Cashbox> findByMerchantIdAndUserCardIdAndDeletedFalse(Long merchantId, Long cardId);

    List<Cashbox> findByDeleted(boolean deleted);

    @Query(nativeQuery = true, value = " select user_card_id from cashbox where id = ?1 ")
    Long findCardById(Long id);
}
