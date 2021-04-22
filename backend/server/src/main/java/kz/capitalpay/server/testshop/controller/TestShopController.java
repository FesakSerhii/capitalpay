package kz.capitalpay.server.testshop.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.testshop.service.TestShopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller(value = "/testshop")
public class TestShopController {

    Logger logger = LoggerFactory.getLogger(TestShopController.class);

    @Autowired
    Gson gson;

    @Autowired
    TestShopService testShopService;

    @GetMapping("/testshop/temporary/page")
    String showTemporaryPage(ModelMap modelMap) {
        Long bilId = testShopService.getNewBillId();
        modelMap.addAttribute("bilId", bilId);
        return "testshoptemppage";
    }

}
