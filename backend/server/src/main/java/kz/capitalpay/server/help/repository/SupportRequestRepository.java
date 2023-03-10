package kz.capitalpay.server.help.repository;

import kz.capitalpay.server.help.model.SupportRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportRequestRepository extends JpaRepository<SupportRequest, Long> {

    List<SupportRequest> findAllByAuthorId(Long id);

    List<SupportRequest> findAllByEmailMessageSentFalse();
}
