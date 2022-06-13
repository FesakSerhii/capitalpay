package kz.capitalpay.server.eventlog.repository;

import kz.capitalpay.server.eventlog.model.PaysystemAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaysystemActionRepository extends JpaRepository<PaysystemAction, Long> {
}
