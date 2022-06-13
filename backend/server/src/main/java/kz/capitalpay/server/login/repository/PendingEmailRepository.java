package kz.capitalpay.server.login.repository;

import kz.capitalpay.server.login.model.PendingEmail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingEmailRepository extends JpaRepository<PendingEmail, String> {

    PendingEmail findByEmail(String email);

    PendingEmail findByConfirmCode(String code);
}
