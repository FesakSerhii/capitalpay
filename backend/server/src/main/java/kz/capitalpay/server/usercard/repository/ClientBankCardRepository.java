package kz.capitalpay.server.usercard.repository;

import kz.capitalpay.server.usercard.model.ClientCardFromBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientBankCardRepository extends JpaRepository<ClientCardFromBank, Long> {

    Optional<ClientCardFromBank> findByOrderId(String orderId);
}
