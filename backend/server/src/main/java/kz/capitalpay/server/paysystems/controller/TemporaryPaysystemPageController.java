package kz.capitalpay.server.paysystems.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TemporaryPaysystemPageController {

    @GetMapping("/paysystems/temporary/page")
    String showTemporaryPage(){
        return "paysystemtemppage";
    }

}
