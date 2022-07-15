package kz.capitalpay.server.test;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.usercard.service.UserCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/api")
public class SaveCardController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SaveCardController.class);
    private final UserCardService userCardService;

    public SaveCardController(UserCardService userCardService) {
        this.userCardService = userCardService;
    }

    @GetMapping("/register-user-card")
    public String registerUserCardWithBank(@RequestParam Long merchantId,
                                           @RequestParam String orderId,
                                           ModelMap modelMap) {
        ResultDTO result = userCardService.registerMerchantCardWithBank(merchantId, orderId);
        Map<String, String> resultMap = (Map<String, String>) result.getData();
        modelMap.addAttribute("xml", resultMap.get("xml"));
        modelMap.addAttribute("backLink", resultMap.get("backLink"));
        modelMap.addAttribute("postLink", resultMap.get("postLink"));
        return "test_register_card";
    }

    @GetMapping("/register-client-card")
    public String registerClientCardWithBank(@RequestParam Long merchantId,
                                             @RequestParam String orderId,
                                             @RequestParam Long cashBoxId,
                                             @RequestParam(required = false) String params,
                                             ModelMap modelMap) {
        ResultDTO result = userCardService.registerClientCardWithBank(merchantId, orderId, cashBoxId, params);
        Map<String, String> resultMap = (Map<String, String>) result.getData();
        modelMap.addAttribute("xml", resultMap.get("xml"));
        modelMap.addAttribute("backLink", resultMap.get("backLink"));
        modelMap.addAttribute("postLink", resultMap.get("postLink"));
        return "test_register_card";
    }

    @ResponseBody
    @PostMapping("/save-card-link")
    public String testPostLink(@RequestBody String body) {
        LOGGER.info("PostLink body: {}", body);
        userCardService.completeBankCardSaving(body);
        return "";
    }
}
