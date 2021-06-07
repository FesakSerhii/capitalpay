package kz.capitalpay.server.login.repository;

import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.model.DeletedApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeletedApplicationUserRepository extends JpaRepository<DeletedApplicationUser, Long> {

    ApplicationUser findByUsername(String username);
}
