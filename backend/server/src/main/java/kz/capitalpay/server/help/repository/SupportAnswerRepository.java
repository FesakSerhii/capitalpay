package kz.capitalpay.server.help.repository;

import kz.capitalpay.server.help.model.SupportAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportAnswerRepository extends JpaRepository<SupportAnswer,Long> {
    List<SupportAnswer> findByRequestId(Long requestId);
}
