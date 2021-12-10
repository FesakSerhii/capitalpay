package kz.capitalpay.server.p2p.repository;

import kz.capitalpay.server.p2p.model.P2pPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface P2pPaymentRepository extends JpaRepository<P2pPayment, String> {

    @Query(value = "select * " +
            " from p2p_payment " +
            " order by timestamp desc " +
            " limit 1 ", nativeQuery = true)
    Optional<P2pPayment> findLast();
}
