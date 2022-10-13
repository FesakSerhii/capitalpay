package kz.capitalpay.server.test;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrDataFactory;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.util.Utils;
import kz.capitalpay.server.constants.HalykControlOrderCommandTypeDictionary;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.p2p.service.P2pService;
import kz.capitalpay.server.paysystems.systems.halyksoap.service.HalykSoapService;
import kz.capitalpay.server.simple.service.SimpleService;
import kz.capitalpay.server.usercard.service.UserCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping(value = "/api", produces = "application/json;charset=UTF-8")
public class PostLinkController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostLinkController.class);
    private final UserCardService userCardService;
    private final P2pService p2pService;
    private final HalykSoapService halykSoapService;
    private final RestTemplate restTemplate;
    private final SimpleService simpleService;

    public PostLinkController(UserCardService userCardService, P2pService p2pService, HalykSoapService halykSoapService, RestTemplate restTemplate, SimpleService simpleService) {
        this.userCardService = userCardService;
        this.p2pService = p2pService;
        this.halykSoapService = halykSoapService;
        this.restTemplate = restTemplate;
        this.simpleService = simpleService;
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
        simpleService.completeBankPurchase(body);
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

    @ResponseBody
    @GetMapping("/test-control-order")
    public String testControlOrder(@RequestParam String orderId,
                                   @RequestParam String amount,
                                   @RequestParam String reference) {
        String url = "https://epay.kkb.kz/jsp/remote/control.jsp?" + halykSoapService.createPurchaseControlXml(
                orderId, amount, 98830654L, reference, HalykControlOrderCommandTypeDictionary.COMPLETE);
        return restTemplate.getForEntity(url, String.class).getBody();
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
