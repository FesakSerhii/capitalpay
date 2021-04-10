package kz.capitalpay.server.help.repository;

import kz.capitalpay.server.help.model.SupportRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportRequestRepository extends JpaRepository<SupportRequest,Long> {
}
