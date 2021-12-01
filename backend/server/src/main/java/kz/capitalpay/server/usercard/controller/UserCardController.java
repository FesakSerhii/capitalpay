package kz.capitalpay.server.usercard.controller;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.usercard.dto.RegisterUserCardDto;
import kz.capitalpay.server.usercard.service.UserCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/user-card", produces = "application/json;charset=UTF-8")
public class UserCardController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserCardController.class);
    private final UserCardService userCardService;
    private final ApplicationUserService applicationUserService;


    public UserCardController(UserCardService userCardService, ApplicationUserService applicationUserService) {
        this.userCardService = userCardService;
        this.applicationUserService = applicationUserService;
    }

    @PostMapping("/register")
    public ResultDTO saveUserCard(@Valid @RequestBody RegisterUserCardDto dto) {
        return userCardService.registerClientCard(dto);
    }

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

    @GetMapping
    public ResultDTO getCardData(@RequestParam String token) {
        return userCardService.getCardData(token);
    }

    @GetMapping("/client-cards")
    public ResultDTO getClientCards() {
        return userCardService.getClientCards();
    }
}
