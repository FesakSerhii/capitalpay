package kz.capitalpay.server.paysystems.systems.secondtestsystem;

import kz.capitalpay.server.paysystems.systems.PaySystem;
import org.springframework.stereotype.Component;

@Component
public class SecondTestSystem implements PaySystem {
    @Override
    public String getComponentName() {
        return "SecondTestSystem";
    }
}
