package kz.capitalpay.server.login.repository;

import kz.capitalpay.server.login.model.TrustIp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrustIpRepository extends JpaRepository<TrustIp, Long> {

    List<TrustIp> findByUserIdAndEnable(Long userId, boolean enable);

    TrustIp findTopByUserIdAndIp(Long userId, String ip);
}
