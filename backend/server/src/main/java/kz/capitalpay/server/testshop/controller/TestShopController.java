package kz.capitalpay.server.testshop.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.testshop.service.TestShopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import static kz.capitalpay.server.simple.service.SimpleService.*;

@Controller
public class TestShopController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestShopController.class);

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

    @GetMapping("/testshop/status")
    String showStatusPage(ModelMap modelMap,
                          @RequestParam String status,
                          @RequestParam String billid,
                          @RequestParam String paymentid,
                          @RequestParam(required = false) String pending) {

        if (pending != null && pending.length() > 0) {
            status = testShopService.checkPendingStatus(paymentid);
        }

        switch (status) {
            case SUCCESS:
                modelMap.addAttribute("color", "green");
                break;
            case FAILED:
                modelMap.addAttribute("color", "red");
                break;
            case PENDING:
                modelMap.addAttribute("color", "yellow");
                break;
        }
        modelMap.addAttribute("billid", billid);
        modelMap.addAttribute("paymentid", paymentid);
        modelMap.addAttribute("status", status);
        return "testshop/paymentstatus";
    }

    @GetMapping("/testshop/success")
    String showSimpleStatusPageSuccess(ModelMap modelMap) {
        modelMap.addAttribute("billid", "");
        modelMap.addAttribute("paymentid", "");
        modelMap.addAttribute("status", "success");
        modelMap.addAttribute("color", "green");
        return "testshop/paymentstatus";
    }

    @GetMapping("/testshop/failed")
    String showSimpleStatusPageFailed(ModelMap modelMap) {
        modelMap.addAttribute("billid", "");
        modelMap.addAttribute("paymentid", "");
        modelMap.addAttribute("status", "failed");
        modelMap.addAttribute("color", "red");
        return "testshop/paymentstatus";
    }


    @PostMapping("/testshop/listener")
    @ResponseBody
    String interactionUrl(@RequestBody String json) {
        LOGGER.info("Interaction url:");
        LOGGER.info(json);
        return "OK";
    }
}
