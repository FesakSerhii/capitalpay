package kz.capitalpay.server.login.repository;

import kz.capitalpay.server.login.model.PendingPhone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PendingPhoneRepository extends JpaRepository<PendingPhone, String> {
    PendingPhone findTopByPhone(String phone);

}
