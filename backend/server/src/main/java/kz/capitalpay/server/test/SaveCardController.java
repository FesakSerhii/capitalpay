package kz.capitalpay.server.test;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.usercard.service.UserCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @ResponseBody
    @PostMapping("/test-post-link")
    public String testPostLink(@RequestBody String body,
                               HttpServletRequest request) {
        LOGGER.info("PostLink body: {}", body);
        LOGGER.info("getRequestURI {}", request.getRequestURI());
        LOGGER.info("getContextPath {}", request.getContextPath());
        LOGGER.info("getPathInfo {}", request.getPathInfo());
        LOGGER.info("getQueryString {}", request.getQueryString());
        LOGGER.info("getServletPath {}", request.getServletPath());
        LOGGER.info("getServletPath {}", request.getParameterMap());
        return "";
    }
}
