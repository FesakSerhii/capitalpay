package kz.capitalpay.server.paysystems.systems;

import kz.capitalpay.server.payments.model.Payment;

public interface PaySystem  {


    String getComponentName();

    String getPaymentButton(Payment payment);

}
