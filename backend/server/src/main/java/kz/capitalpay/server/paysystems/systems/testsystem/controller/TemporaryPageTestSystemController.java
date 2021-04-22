package kz.capitalpay.server.paysystems.systems.testsystem.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class TemporaryPageTestSystemController {

    Logger logger = LoggerFactory.getLogger(TemporaryPageTestSystemController.class);

    @PostMapping("/testsystem/pay")
    String showTestPaymentPage(ModelMap map,
                               @RequestParam String billid,
                               @RequestParam Long totalamount,
                               @RequestParam String currency,
                               HttpServletRequest httpRequest,
                               HttpServletResponse httpServletResponse){



        return "testpaymenttmporary";
    }


}
