package kz.capitalpay.server.usercard.repository;

import kz.capitalpay.server.usercard.model.ClientCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientCardRepository extends JpaRepository<ClientCard, Long> {

    List<ClientCard> findAllByValidTrue();
}
