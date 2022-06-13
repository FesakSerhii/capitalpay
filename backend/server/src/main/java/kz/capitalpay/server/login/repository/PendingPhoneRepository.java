package kz.capitalpay.server.login.repository;

import kz.capitalpay.server.login.model.PendingPhone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingPhoneRepository extends JpaRepository<PendingPhone, String> {

    PendingPhone findTopByPhone(String phone);

    PendingPhone findTopByConfirmCodeAndStatus(String code, String status);

}
