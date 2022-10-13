package kz.capitalpay.server.paymentlink.repository;

import kz.capitalpay.server.paymentlink.model.PaymentLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentLinkRepository extends JpaRepository<PaymentLink, String> {

    List<PaymentLink> findAllByMerchantId(Long merchantId);
}
