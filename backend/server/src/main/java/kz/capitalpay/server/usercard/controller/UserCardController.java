package kz.capitalpay.server.usercard.controller;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.usercard.dto.ChangeMerchantDefaultCardDto;
import kz.capitalpay.server.usercard.dto.DeleteUserCardDto;
import kz.capitalpay.server.usercard.dto.RegisterUserCardDto;
import kz.capitalpay.server.usercard.service.UserCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

import static kz.capitalpay.server.login.service.ApplicationRoleService.ADMIN;

@Controller
@RequestMapping(value = "/api/v1/auth/user-card", produces = "application/json;charset=UTF-8")
public class UserCardController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserCardController.class);
    private final UserCardService userCardService;


    public UserCardController(UserCardService userCardService) {
        this.userCardService = userCardService;
    }

    @ResponseBody
    @PostMapping("/register")
    @RolesAllowed({ADMIN})
    public ResultDTO saveUserCard(@Valid @RequestBody RegisterUserCardDto dto) {
        return userCardService.registerMerchantCard(dto);
    }

    @RolesAllowed({ADMIN})
    @GetMapping("/register-with-bank")
    public String registerUserCardWithBank(@RequestParam Long merchantId,
                                           ModelMap modelMap) {
        ResultDTO result = userCardService.registerMerchantCardWithBank(merchantId);
        Map<String, String> resultMap = (Map<String, String>) result.getData();
        modelMap.addAttribute("xml", resultMap.get("xml"));
        modelMap.addAttribute("backLink", resultMap.get("backLink"));
        modelMap.addAttribute("postLink", resultMap.get("postLink"));
        return "test_register_card";
    }

    @ResponseBody
    @PostMapping("/check-validity/{cardId}")
    public ResultDTO checkCardValidity(@PathVariable Long cardId,
                                       HttpServletRequest httpRequest) {
        String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = httpRequest.getRemoteAddr();
        }
        String userAgent = httpRequest.getHeader("User-Agent");
        return userCardService.checkUserCardValidity(cardId, ipAddress, userAgent);
    }

    @ResponseBody
    @PostMapping("/get")
    public ResultDTO getCardData(@RequestParam String token) {
        return userCardService.getCardData(token);
    }

    @ResponseBody
    @PostMapping("/list")
    public ResultDTO getClientCards(@RequestParam Long merchantId) {
//        return userCardService.getUserCards(merchantId);
        return userCardService.getBankUserCards(merchantId);
    }

    @ResponseBody
    @PostMapping("/change-default-card")
    public ResultDTO changeDefaultCard(@Valid @RequestBody ChangeMerchantDefaultCardDto dto) {
//        return userCardService.changeMerchantDefaultCard(dto);
        return userCardService.changeBankMerchantDefaultCard(dto);
    }

    @ResponseBody
    @PostMapping("/delete")
    public ResultDTO delete(@Valid @RequestBody DeleteUserCardDto dto) {
//        return userCardService.deleteUserCard(dto);
        return userCardService.deleteBankUserCard(dto);
    }
}
