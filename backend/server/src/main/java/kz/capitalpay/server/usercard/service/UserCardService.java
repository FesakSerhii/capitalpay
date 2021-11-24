package kz.capitalpay.server.usercard.service;

import kz.capitalpay.server.constants.ErrorDictionary;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.usercard.dto.RegisterUserCardDto;
import kz.capitalpay.server.usercard.model.UserCard;
import kz.capitalpay.server.usercard.repository.UserCardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class UserCardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserCardService.class);
    private static final String cardHoldingUrl = "http://localhost:8888";

    private final UserCardRepository userCardRepository;
    private final RestTemplate restTemplate;

    public UserCardService(UserCardRepository userCardRepository, RestTemplate restTemplate) {
        this.userCardRepository = userCardRepository;
        this.restTemplate = restTemplate;
    }

    public ResultDTO registerUserCard(RegisterUserCardDto dto, Long userId) {
        ResponseEntity<String> response = restTemplate.postForEntity(cardHoldingUrl + "", dto, String.class);
        String token = response.getBody();
        if (Objects.isNull(token) || token.trim().isEmpty()) {
            return ErrorDictionary.error129;
        }

        UserCard userCard = new UserCard();
        userCard.setUserId(userId);
        userCard.setCardNumber(maskCardNumber(dto.getCardNumber()));
        userCard.setToken(token);
        userCard = userCardRepository.save(userCard);
        return new ResultDTO(true, userCard, 0);
    }

    private String maskCardNumber(String cardNumber) {
        return cardNumber.replaceAll("\\b(\\d{4})(\\d{8})(\\d{4})", "$1XXXXXXXX$3");
    }

}
