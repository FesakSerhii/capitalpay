package kz.capitalpay.server.usercard.repository;

import kz.capitalpay.server.usercard.model.ClientCard;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface ClientCardRepository extends JpaRepository<ClientCard, Long> {

    List<ClientCard> findAllByValidTrueAndDeletedFalse();

    Optional<ClientCard> findByTokenAndValidTrueAndDeletedFalse(String token);
}
