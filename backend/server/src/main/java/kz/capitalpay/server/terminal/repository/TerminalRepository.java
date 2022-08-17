package kz.capitalpay.server.terminal.repository;

import kz.capitalpay.server.terminal.model.Terminal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TerminalRepository extends JpaRepository<Terminal, Long> {

    Optional<Terminal> findByIdAndDeletedFalse(Long id);

    List<Terminal> findAllByDeletedFalse();

    List<Terminal> findAllByDeletedFalseAndFreeTrue();

}
