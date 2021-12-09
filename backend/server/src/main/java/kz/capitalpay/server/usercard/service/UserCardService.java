package kz.capitalpay.server.usercard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.capitalpay.server.constants.ErrorDictionary;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.paysystems.systems.halyksoap.service.HalykSoapService;
import kz.capitalpay.server.usercard.dto.CardDataResponseDto;
import kz.capitalpay.server.usercard.dto.RegisterClientCardDto;
import kz.capitalpay.server.usercard.dto.RegisterUserCardDto;
import kz.capitalpay.server.usercard.model.ClientCard;
import kz.capitalpay.server.usercard.model.UserCard;
import kz.capitalpay.server.usercard.repository.ClientCardRepository;
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
    private final HalykSoapService halykSoapService;
    private final ClientCardRepository clientCardRepository;
    private final ObjectMapper objectMapper;

    public UserCardService(UserCardRepository userCardRepository, RestTemplate restTemplate, HalykSoapService halykSoapService, ClientCardRepository clientCardRepository, ObjectMapper objectMapper) {
        this.userCardRepository = userCardRepository;
        this.restTemplate = restTemplate;
        this.halykSoapService = halykSoapService;
        this.clientCardRepository = clientCardRepository;
        this.objectMapper = objectMapper;
    }

    public ResultDTO registerMerchantCard(RegisterUserCardDto dto) {
        ResponseEntity<String> response = restTemplate.postForEntity(cardHoldingUrl + "/card-data/register",
                dto, String.class);
        String token = response.getBody();
        if (Objects.isNull(token) || token.trim().isEmpty()) {
            return ErrorDictionary.error129;
        }

        UserCard userCard = new UserCard();
        userCard.setCardNumber(maskCardNumber(dto.getCardNumber()));
        userCard.setToken(token);
        userCard.setUserId(dto.getMerchantId());
        userCard = userCardRepository.save(userCard);
        return new ResultDTO(true, userCard, 0);
    }

    public ResultDTO getCardData(String token) {
        CardDataResponseDto dto = getCardDataFromTokenServer(token);
        if (Objects.isNull(dto)) {
            return ErrorDictionary.error130;
        }

        return new ResultDTO(true, dto, 0);
    }

    public UserCard findUserCardByMerchantId(Long userId) {
        return userCardRepository.findByUserId(userId).orElse(null);
    }

    public ClientCard findClientCardById(Long id) {
        return clientCardRepository.findById(id).orElse(null);
    }

    public CardDataResponseDto getCardDataFromTokenServer(String token) {
        String url = cardHoldingUrl + "/card-data?token=" + token;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        CardDataResponseDto dto = null;
        try {
            dto = objectMapper.readValue(response.getBody(), CardDataResponseDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return dto;
    }

    public ResultDTO getClientCards() {
        return new ResultDTO(true, clientCardRepository.findAllByValidTrue(), 0);
    }

    public ResultDTO getUserCards(Long userId) {
        return new ResultDTO(true, userCardRepository.findAllByUserIdAndValidTrue(userId), 0);
    }

    public ResultDTO registerClientCard(RegisterClientCardDto dto) {
        ResponseEntity<String> response = restTemplate.postForEntity(cardHoldingUrl + "/card-data/register",
                dto, String.class);
        String token = response.getBody();
        if (Objects.isNull(token) || token.trim().isEmpty()) {
            return ErrorDictionary.error129;
        }

        ClientCard clientCard = new ClientCard();
        clientCard.setCardNumber(maskCardNumber(dto.getCardNumber()));
        clientCard.setToken(token);
        clientCard.setMerchantId(dto.getMerchantId());
        clientCard = clientCardRepository.save(clientCard);
        return new ResultDTO(true, clientCard, 0);
    }

    public ResultDTO checkClientCardValidity(Long cardId, String ipAddress, String userAgent) {
        ClientCard clientCard = clientCardRepository.findById(cardId).orElse(null);
        if (Objects.isNull(clientCard)) {
            return ErrorDictionary.error130;
        }
        CardDataResponseDto dto = getCardDataFromTokenServer(clientCard.getToken());
        if (Objects.isNull(dto)) {
            return ErrorDictionary.error130;
        }

        boolean valid = halykSoapService.checkCardValidity(ipAddress, userAgent, dto);
        clientCard.setValid(valid);
        clientCardRepository.save(clientCard);
        return new ResultDTO(true, valid, 0);
    }

    public ResultDTO checkUserCardValidity(Long cardId, String ipAddress, String userAgent) {
        UserCard userCard = userCardRepository.findById(cardId).orElse(null);
        if (Objects.isNull(userCard)) {
            return ErrorDictionary.error130;
        }
        CardDataResponseDto dto = getCardDataFromTokenServer(userCard.getToken());
        if (Objects.isNull(dto)) {
            return ErrorDictionary.error130;
        }

        boolean valid = halykSoapService.checkCardValidity(ipAddress, userAgent, dto);
        userCard.setValid(valid);
        userCardRepository.save(userCard);
        return new ResultDTO(true, valid, 0);
    }

    private String maskCardNumber(String cardNumber) {
        return cardNumber.replaceAll("\\b(\\d{4})(\\d{8})(\\d{4})", "$1XXXXXXXX$3");
    }

}
