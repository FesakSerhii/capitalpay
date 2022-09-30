package kz.capitalpay.server.usercard.repository;

import kz.capitalpay.server.usercard.model.UserCardFromBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBankCardRepository extends JpaRepository<UserCardFromBank, Long> {

    Optional<UserCardFromBank> findByOrderId(String orderId);

    Optional<UserCardFromBank> findByToken(String token);

    List<UserCardFromBank> findAllByUserIdAndValidTrueAndDeletedFalse(Long userId);
}
