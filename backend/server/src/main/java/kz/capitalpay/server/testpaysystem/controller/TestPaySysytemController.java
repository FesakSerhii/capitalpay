package kz.capitalpay.server.testpaysystem.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestPaySysytemController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestPaySysytemController.class);

    @GetMapping("/testpaysystem/page")
    String showTemporaryPage() {
        return "testpaysystem/toregistercard";
    }
}
