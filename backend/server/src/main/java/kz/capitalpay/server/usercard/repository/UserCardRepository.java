package kz.capitalpay.server.usercard.repository;

import kz.capitalpay.server.usercard.model.UserCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCardRepository extends JpaRepository<UserCard, Long> {


}
