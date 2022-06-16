package kz.capitalpay.server.login.repository;


import kz.capitalpay.server.login.model.ApplicationRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRoleRepository extends JpaRepository<ApplicationRole, Long> {

    ApplicationRole findByAuthority(String authority);
}
