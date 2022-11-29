package kz.capitalpay.server.paymentlink.repository;

import kz.capitalpay.server.paymentlink.model.PaymentCreationLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentCreationLinkRepository extends JpaRepository<PaymentCreationLink, String> {

    Optional<PaymentCreationLink> findByIdAndDeletedFalse(String id);

    Optional<PaymentCreationLink> findByCashBoxIdAndDeletedFalse(Long cashboxId);

    boolean existsByCashBoxIdAndDeletedFalse(Long cashBoxId);


 }
