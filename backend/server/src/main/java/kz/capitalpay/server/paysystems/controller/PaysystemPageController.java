package kz.capitalpay.server.paysystems.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PaysystemPageController {

    @GetMapping("/paysystems/page")
    String showTemporaryPage(ModelMap modelMap, @RequestParam String paymentid){
        modelMap.addAttribute("paymentId",paymentid);
        return "paysystems/paysystemslist";
    }

}
