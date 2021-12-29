package kz.capitalpay.server.usercard.controller;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.usercard.dto.RegisterClientCardDto;
import kz.capitalpay.server.usercard.service.UserCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/client-card", produces = "application/json;charset=UTF-8")
public class ClientCardController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCardController.class);
    private final UserCardService userCardService;


    public ClientCardController(UserCardService userCardService) {
        this.userCardService = userCardService;
    }

    @PostMapping("/register")
    public ResultDTO saveUserCard(@Valid @RequestBody RegisterClientCardDto dto) {
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

    @PostMapping("/get")
    public ResultDTO getCardData(@RequestParam String token) {
        return userCardService.getCardData(token);
    }

    @PostMapping("/get-client-cards")
    public ResultDTO getClientCards() {
        return userCardService.getClientCards();
    }

    @PostMapping("/delete")
    public ResultDTO deleteClientCard(@RequestParam Long id) {
        return userCardService.deleteClientCard(id);
    }
}
