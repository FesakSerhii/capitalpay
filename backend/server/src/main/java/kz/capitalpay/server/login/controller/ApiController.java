package kz.capitalpay.server.login.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class ApiController {
    @Autowired
    Gson gson;


    @GetMapping("/test")
    String testMyData(Principal principal) {
        return gson.toJson(principal);
    }
}
