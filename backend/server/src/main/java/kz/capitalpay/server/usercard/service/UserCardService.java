package kz.capitalpay.server.usercard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.cashbox.repository.CashboxRepository;
import kz.capitalpay.server.cashbox.service.CashboxService;
import kz.capitalpay.server.constants.ErrorDictionary;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.p2p.dto.P2pSettingsResponseDto;
import kz.capitalpay.server.p2p.model.MerchantP2pSettings;
import kz.capitalpay.server.p2p.service.P2pSettingsService;
import kz.capitalpay.server.paysystems.systems.halyksoap.service.HalykSoapService;
import kz.capitalpay.server.usercard.dto.*;
import kz.capitalpay.server.usercard.model.ClientCard;
import kz.capitalpay.server.usercard.model.UserCard;
import kz.capitalpay.server.usercard.repository.ClientCardRepository;
import kz.capitalpay.server.usercard.repository.UserCardRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserCardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserCardService.class);
    private static final String cardHoldingUrl = "http://localhost:8888";

    private final UserCardRepository userCardRepository;
    private final RestTemplate restTemplate;
    private final HalykSoapService halykSoapService;
    private final ClientCardRepository clientCardRepository;
    private final ObjectMapper objectMapper;
    private final CashboxRepository cashboxRepository;
    private final CashboxService cashboxService;
    private final P2pSettingsService p2pSettingsService;

    public UserCardService(UserCardRepository userCardRepository, RestTemplate restTemplate, HalykSoapService halykSoapService, ClientCardRepository clientCardRepository, ObjectMapper objectMapper, CashboxRepository cashboxRepository, CashboxService cashboxService, P2pSettingsService p2pSettingsService) {
        this.userCardRepository = userCardRepository;
        this.restTemplate = restTemplate;
        this.halykSoapService = halykSoapService;
        this.clientCardRepository = clientCardRepository;
        this.objectMapper = objectMapper;
        this.cashboxRepository = cashboxRepository;
        this.cashboxService = cashboxService;
        this.p2pSettingsService = p2pSettingsService;
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

    public UserCard findUserCardById(Long id) {
        return userCardRepository.findById(id).orElse(null);
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
        clientCard.setCashBoxId(dto.getCashBoxId());
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
        sendClientCardDataToMerchant(clientCard);
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
        setDefaultCashBoxCard(userCard);
        if (!p2pSettingsService.existsByMerchantId(userCard.getUserId())) {
            p2pSettingsService.createMerchantP2pSettings(userCard.getUserId(), userCard.getId());
        }

        CheckCardValidityResponseDto responseDto = new CheckCardValidityResponseDto(valid, userCard.getId());

        return new ResultDTO(true, responseDto, 0);
    }

    public ResultDTO changeMerchantDefaultCard(ChangeMerchantDefaultCardDto dto) {
        MerchantP2pSettings merchantP2pSettings = p2pSettingsService.findP2pSettingsByMerchantId(dto.getMerchantId());
        if (Objects.isNull(merchantP2pSettings)) {
            return ErrorDictionary.error132;
        }
        UserCard userCard = userCardRepository.findById(dto.getCardId()).orElse(null);
        if (Objects.isNull(userCard)) {
            return ErrorDictionary.error130;
        }

        final Long oldDefaultCardId = merchantP2pSettings.getDefaultCardId();
        List<Cashbox> merchantCashBoxesWithDefaultCard = cashboxRepository.findByMerchantIdAndDeletedFalse(dto.getMerchantId())
                .stream()
                .filter(x -> x.getUserCardId().equals(oldDefaultCardId))
                .collect(Collectors.toList());

        List<Cashbox> cashBoxesWithNewDefaultCard = cashboxRepository.findByMerchantIdAndUserCardIdAndDeletedFalse(dto.getMerchantId(), dto.getCardId());
        cashBoxesWithNewDefaultCard.forEach(x -> x.setUseDefaultCard(true));

        merchantCashBoxesWithDefaultCard.forEach(cashbox -> cashbox.setUserCardId(dto.getCardId()));
        cashboxRepository.saveAll(merchantCashBoxesWithDefaultCard);
        cashboxRepository.saveAll(cashBoxesWithNewDefaultCard);
        merchantP2pSettings.setDefaultCardId(dto.getCardId());
        merchantP2pSettings = p2pSettingsService.save(merchantP2pSettings);

        P2pSettingsResponseDto responseDto = new P2pSettingsResponseDto();
        responseDto.setP2pAllowed(merchantP2pSettings.isP2pAllowed());
        responseDto.setMerchantId(merchantP2pSettings.getUserId());
        responseDto.setCardNumber(userCard.getCardNumber());

        return new ResultDTO(true, responseDto, 0);
    }

    private void setDefaultCashBoxCard(UserCard userCard) {
        List<Cashbox> userCashBoxes = cashboxRepository.findByMerchantIdAndDeletedFalse(userCard.getUserId());
        boolean defaultCardExists = userCashBoxes.stream().anyMatch(x -> Objects.nonNull(x.getUserCardId()));
        if (!defaultCardExists) {
            userCashBoxes.forEach(x -> x.setUserCardId(userCard.getId()));
            cashboxRepository.saveAll(userCashBoxes);
        }
    }

    private void sendClientCardDataToMerchant(ClientCard clientCard) {
        try {
            String interactionUrl = cashboxService.getInteractUrl(clientCard.getCashBoxId());
//            String interactionUrl = "http://localhost:8080/testshop/listener";
            CardDataForMerchantDto detailsJson = generateClientCardResponseDto(clientCard);
            Map<String, Object> requestJson = new HashMap<>();
            requestJson.put("type", "registerClientCard");
            requestJson.put("data", detailsJson);
            String response = restTemplate.postForObject(interactionUrl,
                    requestJson, String.class, java.util.Optional.ofNullable(null));
            LOGGER.info(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String maskCardNumber(String cardNumber) {
        return cardNumber.replaceAll("\\b(\\d{4})(\\d{8})(\\d{4})", "$1********$3");
    }

    private CardDataForMerchantDto generateClientCardResponseDto(ClientCard clientCard) {
        CardDataForMerchantDto dto = new CardDataForMerchantDto();
        String secret = cashboxService.getSecret(clientCard.getCashBoxId());
        dto.setCardId(clientCard.getId());
        dto.setCardNumber(clientCard.getCardNumber());
        dto.setToken(clientCard.getToken());
        dto.setSignature(DigestUtils.sha256Hex(clientCard.getId() + clientCard.getToken() + clientCard.getCardNumber() + secret));
        return dto;
    }

}
