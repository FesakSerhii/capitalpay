package kz.capitalpay.server.testshop.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.testshop.service.TestShopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static kz.capitalpay.server.simple.service.SimpleService.*;

@Controller
public class TestShopController {

    Logger logger = LoggerFactory.getLogger(TestShopController.class);

    @Autowired
    Gson gson;

    @Autowired
    TestShopService testShopService;

    @GetMapping("/testshop/page")
    String showTemporaryPage(ModelMap modelMap) {
        Long bilId = testShopService.getNewBillId();
        modelMap.addAttribute("bilId", bilId);
        return "testshop/store";
    }

    @GetMapping("/testshop/temporary/status")
    String showStatusPage(ModelMap modelMap,
                          @RequestParam String status,
                          @RequestParam String billid) {

        switch (status){
            case SUCCESS:    modelMap.addAttribute("color", "green"); break;
            case FAILED:    modelMap.addAttribute("color", "red"); break;
            case PENDING:    modelMap.addAttribute("color", "yellow"); break;
        }

        modelMap.addAttribute("billid", billid);
        modelMap.addAttribute("status", status);
        return "testshopstatus";
    }

}
