package kz.capitalpay.server.paysystems.systems.testsystem;

import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.paysystems.systems.PaySystem;
import kz.capitalpay.server.paysystems.systems.testsystem.service.TestSystemInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestSystem implements PaySystem {

    @Autowired
    TestSystemInService testSystemInService;

    @Override
    public String getComponentName() {
        return "TestSystem";
    }

    @Override
    public String getPaymentButton(Payment payment) {
        return testSystemInService.getPaymentButton(payment);
    }
}
