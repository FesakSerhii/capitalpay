package kz.capitalpay.server.paysystems.systems.testsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TemporaryPageTestSystemController {

    @GetMapping("/testsystem/pay")
    String showTestPaymentPage(ModelMap map){
        return "testpaymenttmporary";
    }


}
