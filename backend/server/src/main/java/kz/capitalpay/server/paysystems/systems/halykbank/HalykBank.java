//package kz.capitalpay.server.paysystems.systems.halykbank;
//
//import kz.capitalpay.server.payments.model.Payment;
//import kz.capitalpay.server.paysystems.systems.PaySystem;
//import kz.capitalpay.server.paysystems.systems.halykbank.sevice.HalykService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class HalykBank implements PaySystem {
//
//    @Autowired
//    HalykService halykService;
//
//    @Override
//    public String getComponentName() {
//        return "HalykBank";
//    }
//
//    @Override
//    public String getPaymentButton(Payment payment) {
//        return halykService.getPaymentButton(payment);
//    }
//}
