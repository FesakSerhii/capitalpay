package kz.capitalpay.server.paysystems.repository;

import kz.capitalpay.server.paysystems.model.Paysystem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaysystemRepository extends JpaRepository<Paysystem,Long> {
}
