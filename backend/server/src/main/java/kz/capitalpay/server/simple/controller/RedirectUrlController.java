package kz.capitalpay.server.simple.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectUrlController {

    @GetMapping("/payment/simple/redirecturl")
    void getRedirectUrl(){

    }
}
