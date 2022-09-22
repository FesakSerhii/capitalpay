package kz.capitalpay.server.usercard.controller;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.usercard.dto.RegisterClientCardDto;
import kz.capitalpay.server.usercard.service.UserCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping(value = "/api/v1/client-card", produces = "application/json;charset=UTF-8")
public class ClientCardController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCardController.class);
    private static final String REGISTER_CARD_URL = "https://card.capitalpay.kz";
    private final UserCardService userCardService;


    public ClientCardController(UserCardService userCardService) {
        this.userCardService = userCardService;
    }

    @ResponseBody
    @PostMapping("/register")
    public ResultDTO saveUserCard(@Valid @RequestBody RegisterClientCardDto dto) {
        return userCardService.registerClientCard(dto);
    }

    @GetMapping("/register")
    public String registerClientCardWithBank(@RequestParam Long merchantId,
                                             @RequestParam Long cashBoxId,
                                             @RequestParam(required = false) String parameters,
                                             ModelMap modelMap) {
        LOGGER.info("/api/v1/client-card/register");
        LOGGER.info("merchantId {}", merchantId);
        LOGGER.info("cashBoxId {}", cashBoxId);
        LOGGER.info("params {}", parameters);
        ResultDTO result = userCardService.registerClientCardWithBank(merchantId, cashBoxId, parameters);
        Map<String, String> resultMap = (Map<String, String>) result.getData();
        modelMap.addAttribute("xml", resultMap.get("xml"));
        modelMap.addAttribute("backLink", resultMap.get("backLink"));
        modelMap.addAttribute("postLink", resultMap.get("postLink"));
        return "register_card";
    }

//    @GetMapping("/register")
//    public RedirectView registerClientCardWithBank(@RequestParam Long merchantId,
//                                                   @RequestParam Long cashBoxId,
//                                                   @RequestParam(required = false) String params,
//                                                   RedirectAttributes attributes) {
//        attributes.addAttribute("merchantId", merchantId);
//        attributes.addAttribute("cashBoxId", cashBoxId);
//        if (Objects.nonNull(params)) {
//            attributes.addAttribute("params", params);
//        }
//        return new RedirectView(REGISTER_CARD_URL);
//    }

    @ResponseBody
    @PostMapping("/check-validity/{cardId}")
    public ResultDTO checkCardValidity(@PathVariable Long cardId,
                                       HttpServletRequest httpRequest) {
        String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = httpRequest.getRemoteAddr();
        }
        String userAgent = httpRequest.getHeader("User-Agent");
        return userCardService.checkClientCardValidity(cardId, ipAddress, userAgent);
    }

    @ResponseBody
    @PostMapping("/get")
    public ResultDTO getCardData(@RequestParam String token) {
        return userCardService.getCardData(token);
    }

    @ResponseBody
    @PostMapping("/get-client-cards")
    public ResultDTO getClientCards() {
//        return userCardService.getClientCards();
        return userCardService.getBankClientCards();
    }

    @ResponseBody
    @PostMapping("/delete/{id}")
    public ResultDTO deleteClientCard(@PathVariable Long id) {
//        return userCardService.deleteClientCard(id);
        return userCardService.deleteBankClientCard(id);
    }
}
