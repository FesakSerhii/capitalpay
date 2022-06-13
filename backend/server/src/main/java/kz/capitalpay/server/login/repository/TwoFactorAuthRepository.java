package kz.capitalpay.server.login.repository;

import kz.capitalpay.server.login.model.TwoFactorAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TwoFactorAuthRepository extends JpaRepository<TwoFactorAuth, Long> {
}
