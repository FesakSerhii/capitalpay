package kz.capitalpay.server.p2p.repository;

import kz.capitalpay.server.p2p.model.MerchantP2pSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface P2pSettingsRepository extends JpaRepository<MerchantP2pSettings, Long> {

    boolean existsByUserId(Long userId);

    Optional<MerchantP2pSettings> findByUserId(Long userId);
}
