package kz.capitalpay.server.eventlog.repository;

import kz.capitalpay.server.eventlog.model.OperatorsAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperatorActionRepository extends JpaRepository<OperatorsAction,Long> {

}
