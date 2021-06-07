package kz.capitalpay.server.paysystems.systems.testsystem.repository;

import kz.capitalpay.server.paysystems.systems.testsystem.model.TestsystemPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestsystemPaymentRepository extends JpaRepository<TestsystemPayment,String> {

}
