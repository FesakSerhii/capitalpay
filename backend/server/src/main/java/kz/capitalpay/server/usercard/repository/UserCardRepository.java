package kz.capitalpay.server.usercard.repository;

import kz.capitalpay.server.usercard.model.UserCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCardRepository extends JpaRepository<UserCard, Long> {

    List<UserCard> findAllByUserIdAndValidTrue(Long userId);

    Optional<UserCard> findByUserId(Long userId);
}
