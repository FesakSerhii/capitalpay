package kz.capitalpay.server.test;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.p2p.service.P2pService;
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
    private final P2pService p2pService;

    public SaveCardController(UserCardService userCardService, P2pService p2pService) {
        this.userCardService = userCardService;
        this.p2pService = p2pService;
    }

//    @GetMapping("/register-user-card")
//    public String registerUserCardWithBank(@RequestParam Long merchantId,
//                                           ModelMap modelMap) {
//        ResultDTO result = userCardService.registerMerchantCardWithBank();
//        Map<String, String> resultMap = (Map<String, String>) result.getData();
//        modelMap.addAttribute("xml", resultMap.get("xml"));
//        modelMap.addAttribute("backLink", resultMap.get("backLink"));
//        modelMap.addAttribute("postLink", resultMap.get("postLink"));
//        return "test_register_card";
//    }

    @GetMapping("/register-client-card")
    public String registerClientCardWithBank(@RequestParam Long merchantId, @RequestParam Long cashBoxId, @RequestParam(required = false) String params, ModelMap modelMap) {
        ResultDTO result = userCardService.registerClientCardWithBank(merchantId, cashBoxId, params);
        Map<String, String> resultMap = (Map<String, String>) result.getData();
        modelMap.addAttribute("xml", resultMap.get("xml"));
        modelMap.addAttribute("backLink", resultMap.get("backLink"));
        modelMap.addAttribute("postLink", resultMap.get("postLink"));
        return "register_card";
    }

    @ResponseBody
    @PostMapping("/save-card-link")
    public String testPostLink(@RequestBody String body) {
        LOGGER.info("PostLink body: {}", body);
        userCardService.completeBankCardSaving(body);
        return "0";
    }

    @ResponseBody
    @PostMapping("/p2p-link")
    public String testP2pPostLink(@RequestBody String body) {
        LOGGER.info("/p2p-link");
        LOGGER.info("PostLink body: {}", body);
        p2pService.completeBankAnonymousP2p(body);
        return "0";
    }

    @ResponseBody
    @PostMapping("/purchase-link")
    public String purchasePostLink(@RequestBody String body) {
        LOGGER.info("/purchase-link");
        LOGGER.info("PostLink body: {}", body);
        p2pService.completeBankPurchase(body);
        return "0";
    }

    @ResponseBody
    @PostMapping("/send-p2p")
    public String sendP2p() {
        LOGGER.info("sendP2p()");
        userCardService.sendTestP2p();
        return "";
    }

    @GetMapping("/send-anonymous-p2p")
    public String sendAnonymousP2p(ModelMap modelMap) {
        LOGGER.info("sendAnonymousP2p()");
        ResultDTO result = userCardService.sendAnonymousTestP2p();
        Map<String, String> resultMap = (Map<String, String>) result.getData();
        modelMap.addAttribute("xml", resultMap.get("xml"));
        modelMap.addAttribute("backLink", resultMap.get("backLink"));
        modelMap.addAttribute("postLink", resultMap.get("postLink"));
        return "p2p";
    }

    @GetMapping("/buy-something")
    public String sendPurchaseRequest(ModelMap modelMap) {
        LOGGER.info("sendPurchaseRequest()");
        ResultDTO result = userCardService.sendTestPurchase();
        Map<String, String> resultMap = (Map<String, String>) result.getData();
        modelMap.addAttribute("xml", resultMap.get("xml"));
        modelMap.addAttribute("backLink", resultMap.get("backLink"));
        modelMap.addAttribute("postLink", resultMap.get("postLink"));
        return "purchase";
    }

//    @GetMapping("/stream-sse")
//    public Flux<ServerSentEvent<String>> streamEvents() {
//        return Flux.interval(Duration.ofSeconds(1))
//                .map(sequence -> ServerSentEvent.<String>builder()
//                        .comment("comment")
//                        .id(String.valueOf(sequence))
//                        .event("periodic-event")
//                        .data("SSE - " + LocalTime.now().toString())
//                        .build());
//    }
}
