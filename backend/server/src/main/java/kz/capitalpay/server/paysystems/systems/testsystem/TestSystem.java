package kz.capitalpay.server.paysystems.systems.testsystem;

import kz.capitalpay.server.paysystems.systems.PaySystem;
import org.springframework.stereotype.Component;

@Component
public class TestSystem implements PaySystem {

    @Override
    public String getComponentName() {
        return "TestSystem";
    }
}
