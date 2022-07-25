package kz.capitalpay.server.usercard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.cashbox.repository.CashboxRepository;
import kz.capitalpay.server.cashbox.service.CashboxService;
import kz.capitalpay.server.constants.ErrorDictionary;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.merchantsettings.service.CashboxSettingsService;
import kz.capitalpay.server.p2p.dto.P2pSettingsResponseDto;
import kz.capitalpay.server.p2p.model.MerchantP2pSettings;
import kz.capitalpay.server.p2p.service.P2pSettingsService;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.payments.service.PaymentService;
import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykSaveCardOrder;
import kz.capitalpay.server.paysystems.systems.halyksoap.service.HalykSoapService;
import kz.capitalpay.server.usercard.dto.*;
import kz.capitalpay.server.usercard.model.ClientCard;
import kz.capitalpay.server.usercard.model.ClientCardFromBank;
import kz.capitalpay.server.usercard.model.UserCard;
import kz.capitalpay.server.usercard.model.UserCardFromBank;
import kz.capitalpay.server.usercard.repository.ClientBankCardRepository;
import kz.capitalpay.server.usercard.repository.ClientCardRepository;
import kz.capitalpay.server.usercard.repository.UserBankCardRepository;
import kz.capitalpay.server.usercard.repository.UserCardRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static kz.capitalpay.server.merchantsettings.service.CashboxSettingsService.REDIRECT_FAILED_URL;
import static kz.capitalpay.server.merchantsettings.service.CashboxSettingsService.REDIRECT_SUCCESS_URL;

@Service
public class UserCardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserCardService.class);
    private static final String cardHoldingUrl = "http://localhost:8888";
    private static final String REGISTER_MERCHANT_CARD_REDIRECT_URL = "https://admin.capitalpay.kz/admin-panel/card-check";

    private final UserCardRepository userCardRepository;
    private final RestTemplate restTemplate;
    private final HalykSoapService halykSoapService;
    private final ClientCardRepository clientCardRepository;
    private final ObjectMapper objectMapper;
    private final CashboxRepository cashboxRepository;
    private final CashboxService cashboxService;
    private final P2pSettingsService p2pSettingsService;
    private final PaymentService paymentService;
    private final UserBankCardRepository userBankCardRepository;
    private final ClientBankCardRepository clientBankCardRepository;
    private final CashboxSettingsService cashboxSettingsService;

    @Value("${server.test}")
    private boolean isTestServer;

    public UserCardService(UserCardRepository userCardRepository, RestTemplate restTemplate, HalykSoapService halykSoapService, ClientCardRepository clientCardRepository, ObjectMapper objectMapper, CashboxRepository cashboxRepository, CashboxService cashboxService, P2pSettingsService p2pSettingsService, PaymentService paymentService, UserBankCardRepository userBankCardRepository, ClientBankCardRepository clientBankCardRepository, CashboxSettingsService cashboxSettingsService) {
        this.userCardRepository = userCardRepository;
        this.restTemplate = restTemplate;
        this.halykSoapService = halykSoapService;
        this.clientCardRepository = clientCardRepository;
        this.objectMapper = objectMapper;
        this.cashboxRepository = cashboxRepository;
        this.cashboxService = cashboxService;
        this.p2pSettingsService = p2pSettingsService;
        this.paymentService = paymentService;
        this.userBankCardRepository = userBankCardRepository;
        this.clientBankCardRepository = clientBankCardRepository;
        this.cashboxSettingsService = cashboxSettingsService;
    }

    public ResultDTO registerMerchantCard(RegisterUserCardDto dto) {
//        MerchantP2pSettings merchantP2pSettings = p2pSettingsService.findP2pSettingsByMerchantId(dto.getMerchantId());
//        if (Objects.isNull(merchantP2pSettings) || !merchantP2pSettings.isP2pAllowed()) {
//            return ErrorDictionary.error134;
//        }

        ResponseEntity<String> response = restTemplate.postForEntity(cardHoldingUrl + "/card-data/register",
                dto, String.class);
        String token = response.getBody();
        if (Objects.isNull(token) || token.trim().isEmpty()) {
            return ErrorDictionary.CARD_REGISTRATION_ERROR;
        }

        if (!isTestServer && userCardRepository.existsByUserIdAndToken(dto.getMerchantId(), token)) {
            return ErrorDictionary.CARD_ALREADY_EXISTS;
        }

        UserCard userCard = new UserCard();
        userCard.setCardNumber(maskCardNumber(dto.getCardNumber()));
        userCard.setToken(token);
        userCard.setUserId(dto.getMerchantId());
        userCard = userCardRepository.save(userCard);
        return new ResultDTO(true, userCard, 0);
    }

    public ResultDTO registerMerchantCardWithBank(Long merchantId) {
        Payment payment = paymentService.generateSaveBankCardPayment();
        UserCardFromBank userCardFromBank = new UserCardFromBank();
        userCardFromBank.setOrderId(payment.getPaySysPayId());
        userCardFromBank.setUserId(merchantId);
        userCardFromBank.setToken(UUID.randomUUID().toString());
        userBankCardRepository.save(userCardFromBank);
        String saveCardXml = halykSoapService.createSaveCardXml(payment.getPaySysPayId(), merchantId, true);
        return registerCardFromBank(saveCardXml, null,
                REGISTER_MERCHANT_CARD_REDIRECT_URL.concat(String.format(
                        "?userId=%s&orderId=%s",
                        merchantId, payment.getPaySysPayId())
                )
        );
    }

    public ResultDTO registerClientCardWithBank(Long merchantId, Long cashBoxId, String params) {
        Cashbox cashbox = cashboxService.findById(cashBoxId);
        if (!cashbox.isP2pAllowed()) {
            return ErrorDictionary.P2P_IS_NOT_ALLOWED;
        }
        Map<String, String> resultUrls = cashboxSettingsService.getMerchantResultUrls(cashbox.getId());

        Payment payment = paymentService.generateSaveBankCardPayment();
        ClientCardFromBank clientCardFromBank = new ClientCardFromBank();
        clientCardFromBank.setOrderId(payment.getPaySysPayId());
        clientCardFromBank.setCashBoxId(cashBoxId);
        clientCardFromBank.setMerchantId(merchantId);
        clientCardFromBank.setParams(params);
        clientCardFromBank.setToken(UUID.randomUUID().toString());
        clientBankCardRepository.save(clientCardFromBank);
        String saveCardXml = halykSoapService.createSaveCardXml(payment.getPaySysPayId(), merchantId, false);
        return registerCardFromBank(saveCardXml, resultUrls, null);
    }

    public void completeBankCardSaving(String requestBody) {
        LOGGER.info("completeBankCardSaving()");
        if (Objects.isNull(requestBody) || requestBody.trim().isEmpty()) {
            LOGGER.info("requestBody is NULL");
            return;
        }
        String xml = requestBody.replace("response=", "");
        HalykSaveCardOrder halykSaveCardOrder = halykSoapService.parseSaveCardWithBankXml(xml);
        if (Objects.isNull(halykSaveCardOrder)) {
            LOGGER.info("halykSaveCardOrder is NULL");
            return;
        }
        if (halykSaveCardOrder.getResponseServiceId().equals("true")) {
            setUserCardFromBankData(halykSaveCardOrder);
        } else {
            setClientCardFromBankData(halykSaveCardOrder);
        }
    }

    private void setUserCardFromBankData(HalykSaveCardOrder halykSaveCardOrder) {
        UserCardFromBank userCardFromBank = userBankCardRepository.findByOrderId(halykSaveCardOrder.getOrderId()).orElse(null);
        if (Objects.isNull(userCardFromBank)) {
            LOGGER.info("userCardFromBank is NULL");
            return;
        }
        userCardFromBank.setValid(true);
        userCardFromBank.setBankCardId(halykSaveCardOrder.getCardId());
        userCardFromBank.setCardNumber(maskCardFromBank(halykSaveCardOrder.getCardHash()));
        userBankCardRepository.save(userCardFromBank);
    }

    private void setClientCardFromBankData(HalykSaveCardOrder halykSaveCardOrder) {
        ClientCardFromBank clientCardFromBank = clientBankCardRepository.findByOrderId(halykSaveCardOrder.getOrderId()).orElse(null);
        if (Objects.isNull(clientCardFromBank)) {
            LOGGER.info("userCardFromBank is NULL");
            return;
        }
        clientCardFromBank.setValid(true);
        clientCardFromBank.setBankCardId(halykSaveCardOrder.getCardId());
        clientCardFromBank.setCardNumber(maskCardFromBank(halykSaveCardOrder.getCardHash()));
        clientCardFromBank = clientBankCardRepository.save(clientCardFromBank);
        sendClientCardDataToMerchant(clientCardFromBank);
    }

    private ResultDTO registerCardFromBank(String saveCardXml, Map<String, String> resultUrls, String backLink) {
        LOGGER.info("saveCardXml {}", saveCardXml);
        String encodedXml = Base64.getEncoder().encodeToString(saveCardXml.getBytes());
        Map<String, String> result = new HashMap<>();
        result.put("xml", encodedXml);
        if (Objects.isNull(backLink) && Objects.nonNull(resultUrls)) {
            result.put("backLink", resultUrls.get(REDIRECT_SUCCESS_URL));
            result.put("FailureBackLink", resultUrls.get(REDIRECT_FAILED_URL));
        } else {
            result.put("backLink", backLink);
        }
        result.put("postLink", "https://api.capitalpay.kz/api/save-card-link");
        result.put("action", "https://testpay.kkb.kz/jsp/hbpay/logon.jsp");
        return new ResultDTO(true, result, 0);
    }

    public ResultDTO getCardData(String token) {
        CardDataResponseDto dto = getCardDataFromTokenServer(token);
        if (Objects.isNull(dto)) {
            return ErrorDictionary.CARD_NOT_FOUND;
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

    public ClientCard findClientCardByToken(String token) {
        return clientCardRepository.findByTokenAndValidTrueAndDeletedFalse(token).orElse(null);
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
        return new ResultDTO(true, clientCardRepository.findAllByValidTrueAndDeletedFalse(), 0);
    }

    public ResultDTO getBankClientCards() {
        return new ResultDTO(true, clientBankCardRepository.findAllByValidTrueAndDeletedFalse(), 0);
    }

    public ResultDTO getUserCards(Long userId) {
        return new ResultDTO(true, userCardRepository.findAllByUserIdAndValidTrueAndDeletedFalse(userId), 0);
    }

    public ResultDTO getBankUserCards(Long userId) {
        return new ResultDTO(true, userBankCardRepository.findAllByUserIdAndValidTrueAndDeletedFalse(userId), 0);
    }

    public ResultDTO registerClientCard(RegisterClientCardDto dto) {
        Cashbox cashbox = cashboxService.findById(dto.getCashBoxId());
        if (!cashbox.isP2pAllowed()) {
            return ErrorDictionary.P2P_IS_NOT_ALLOWED;
        }

        ResponseEntity<String> response = restTemplate.postForEntity(cardHoldingUrl + "/card-data/register",
                dto, String.class);
        String token = response.getBody();
        if (Objects.isNull(token) || token.trim().isEmpty()) {
            return ErrorDictionary.CARD_REGISTRATION_ERROR;
        }

        ClientCard clientCard = new ClientCard();
        clientCard.setCardNumber(maskCardNumber(dto.getCardNumber()));
        clientCard.setToken(token);
        clientCard.setCashBoxId(dto.getCashBoxId());
        clientCard.setMerchantId(dto.getMerchantId());
        clientCard.setParams(dto.getParameters());
        clientCard = clientCardRepository.save(clientCard);
        return new ResultDTO(true, clientCard, 0);
    }

    public ResultDTO checkClientCardValidity(Long cardId, String ipAddress, String userAgent) {
        ClientCard clientCard = clientCardRepository.findById(cardId).orElse(null);
        if (Objects.isNull(clientCard)) {
            return ErrorDictionary.CARD_NOT_FOUND;
        }

        Cashbox cashbox = cashboxService.findById(clientCard.getCashBoxId());
        if (!cashbox.isP2pAllowed()) {
            return ErrorDictionary.P2P_IS_NOT_ALLOWED;
        }

        CardDataResponseDto dto = getCardDataFromTokenServer(clientCard.getToken());
        if (Objects.isNull(dto)) {
            return ErrorDictionary.CARD_NOT_FOUND;
        }

        CheckCardValidityResponse response = halykSoapService.checkCardValidity(ipAddress, userAgent, dto);
        clientCard.setValid(response.isValid());
        clientCardRepository.save(clientCard);
        if (response.isValid()) {
            sendClientCardDataToMerchant(clientCard);
        }

        CheckCardValidityResponseDto responseDto = new CheckCardValidityResponseDto(response.isValid(), clientCard.getId(), response.getReturnCode());

        return new ResultDTO(true, responseDto, 0);
    }

    public ResultDTO checkUserCardValidity(Long cardId, String ipAddress, String userAgent) {
        UserCard userCard = userCardRepository.findById(cardId).orElse(null);
        if (Objects.isNull(userCard)) {
            return ErrorDictionary.CARD_NOT_FOUND;
        }
//        MerchantP2pSettings merchantP2pSettings = p2pSettingsService.findP2pSettingsByMerchantId(userCard.getUserId());
//        if (Objects.isNull(merchantP2pSettings) || !merchantP2pSettings.isP2pAllowed()) {
//            return ErrorDictionary.error134;
//        }

        CardDataResponseDto dto = getCardDataFromTokenServer(userCard.getToken());
        if (Objects.isNull(dto)) {
            return ErrorDictionary.CARD_NOT_FOUND;
        }

        CheckCardValidityResponse response = halykSoapService.checkCardValidity(ipAddress, userAgent, dto);
        userCard.setValid(response.isValid());
        userCardRepository.save(userCard);
        setDefaultCashBoxCard(userCard);
        if (!p2pSettingsService.existsByMerchantId(userCard.getUserId())) {
            p2pSettingsService.createMerchantP2pSettings(userCard.getUserId(), userCard.getId());
        }

        CheckCardValidityResponseDto responseDto = new CheckCardValidityResponseDto(response.isValid(), userCard.getId(), response.getReturnCode());
        return new ResultDTO(true, responseDto, 0);
    }

    public ResultDTO changeMerchantDefaultCard(ChangeMerchantDefaultCardDto dto) {
        MerchantP2pSettings merchantP2pSettings = p2pSettingsService.findP2pSettingsByMerchantId(dto.getMerchantId());
        if (Objects.isNull(merchantP2pSettings)) {
            return ErrorDictionary.P2P_SETTINGS_NOT_FOUND;
        }
        UserCard userCard = userCardRepository.findById(dto.getCardId()).orElse(null);
        if (Objects.isNull(userCard)) {
            return ErrorDictionary.CARD_NOT_FOUND;
        }

        if (!merchantP2pSettings.isP2pAllowed()) {
            return ErrorDictionary.P2P_IS_NOT_ALLOWED;
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

    public ResultDTO changeBankMerchantDefaultCard(ChangeMerchantDefaultCardDto dto) {
        MerchantP2pSettings merchantP2pSettings = p2pSettingsService.findP2pSettingsByMerchantId(dto.getMerchantId());
        if (Objects.isNull(merchantP2pSettings)) {
            return ErrorDictionary.P2P_SETTINGS_NOT_FOUND;
        }
        UserCardFromBank userCard = userBankCardRepository.findById(dto.getCardId()).orElse(null);
        if (Objects.isNull(userCard)) {
            return ErrorDictionary.CARD_NOT_FOUND;
        }

        if (!merchantP2pSettings.isP2pAllowed()) {
            return ErrorDictionary.P2P_IS_NOT_ALLOWED;
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

    public ResultDTO deleteUserCard(DeleteUserCardDto dto) {
        UserCard userCard = userCardRepository.findById(dto.getCardId()).orElse(null);
        if (Objects.isNull(userCard)) {
            return ErrorDictionary.CARD_NOT_FOUND;
        }
        if (!userCard.getUserId().equals(dto.getMerchantId())) {
            return ErrorDictionary.AVAILABLE_ONLY_FOR_CASHBOX_OWNER;
        }

        MerchantP2pSettings merchantP2pSettings = p2pSettingsService.findP2pSettingsByMerchantId(dto.getMerchantId());
        if (Objects.isNull(merchantP2pSettings) || !merchantP2pSettings.isP2pAllowed()) {
            return ErrorDictionary.P2P_IS_NOT_ALLOWED;
        }

        userCard.setDeleted(true);
        userCardRepository.save(userCard);
        return new ResultDTO(true, true, 0);
    }

    public ResultDTO deleteBankUserCard(DeleteUserCardDto dto) {
        UserCardFromBank userCard = userBankCardRepository.findById(dto.getCardId()).orElse(null);
        if (Objects.isNull(userCard)) {
            return ErrorDictionary.CARD_NOT_FOUND;
        }
        if (!userCard.getUserId().equals(dto.getMerchantId())) {
            return ErrorDictionary.AVAILABLE_ONLY_FOR_CASHBOX_OWNER;
        }

        MerchantP2pSettings merchantP2pSettings = p2pSettingsService.findP2pSettingsByMerchantId(dto.getMerchantId());
        if (Objects.isNull(merchantP2pSettings) || !merchantP2pSettings.isP2pAllowed()) {
            return ErrorDictionary.P2P_IS_NOT_ALLOWED;
        }

        userCard.setDeleted(true);
        userBankCardRepository.save(userCard);
        return new ResultDTO(true, true, 0);
    }

    public ResultDTO deleteClientCard(Long cardId) {
        ClientCard clientCard = clientCardRepository.findById(cardId).orElse(null);
        if (Objects.isNull(clientCard)) {
            return ErrorDictionary.CARD_NOT_FOUND;
        }

        MerchantP2pSettings merchantP2pSettings = p2pSettingsService.findP2pSettingsByMerchantId(clientCard.getMerchantId());
        if (Objects.isNull(merchantP2pSettings) || !merchantP2pSettings.isP2pAllowed()) {
            return ErrorDictionary.P2P_IS_NOT_ALLOWED;
        }

        Cashbox cashbox = cashboxService.findById(clientCard.getCashBoxId());
        if (!cashbox.isP2pAllowed()) {
            return ErrorDictionary.P2P_IS_NOT_ALLOWED;
        }

        clientCard.setDeleted(true);
        clientCardRepository.save(clientCard);
        return new ResultDTO(true, true, 0);
    }

    public ResultDTO deleteBankClientCard(Long cardId) {
        ClientCardFromBank clientCard = clientBankCardRepository.findById(cardId).orElse(null);
        if (Objects.isNull(clientCard)) {
            return ErrorDictionary.CARD_NOT_FOUND;
        }

        MerchantP2pSettings merchantP2pSettings = p2pSettingsService.findP2pSettingsByMerchantId(clientCard.getMerchantId());
        if (Objects.isNull(merchantP2pSettings) || !merchantP2pSettings.isP2pAllowed()) {
            return ErrorDictionary.P2P_IS_NOT_ALLOWED;
        }

        Cashbox cashbox = cashboxService.findById(clientCard.getCashBoxId());
        if (!cashbox.isP2pAllowed()) {
            return ErrorDictionary.P2P_IS_NOT_ALLOWED;
        }

        clientCard.setDeleted(true);
        clientBankCardRepository.save(clientCard);
        return new ResultDTO(true, true, 0);
    }

    public UserCardFromBank findUserCardFromBankByToken(String token) {
        return userBankCardRepository.findByToken(token).orElse(null);
    }

    public UserCardFromBank findUserCardFromBankById(Long id) {
        return userBankCardRepository.findById(id).orElse(null);
    }

    public ClientCardFromBank findClientCardFromBankByToken(String token) {
        return clientBankCardRepository.findByToken(token).orElse(null);
    }

    public ClientCardFromBank findClientCardFromBankById(Long id) {
        return clientBankCardRepository.findById(id).orElse(null);
    }

    public ResultDTO checkBankUserCardValidation(String orderId) {
        UserCardFromBank userCardFromBank = userBankCardRepository.findByOrderId(orderId).orElse(null);
        if (Objects.isNull(userCardFromBank)) {
            return ErrorDictionary.CARD_NOT_FOUND;
        }
        return new ResultDTO(true, true, 0);
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

    private void sendClientCardDataToMerchant(ClientCardFromBank clientCard) {
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

    private String maskCardFromBank(String number) {
        return number.replace("-", "").replace("X", "*");
    }

    private CardDataForMerchantDto generateClientCardResponseDto(ClientCard clientCard) {
        CardDataForMerchantDto dto = new CardDataForMerchantDto();
        String secret = cashboxService.getSecret(clientCard.getCashBoxId());
        dto.setCardId(clientCard.getId());
        dto.setCardNumber(clientCard.getCardNumber());
        dto.setToken(clientCard.getToken());
        dto.setParams(clientCard.getParams());
        dto.setSignature(DigestUtils.sha256Hex(clientCard.getId() + clientCard.getToken() + clientCard.getCardNumber() + secret));
        return dto;
    }

    private CardDataForMerchantDto generateClientCardResponseDto(ClientCardFromBank clientCard) {
        CardDataForMerchantDto dto = new CardDataForMerchantDto();
        String secret = cashboxService.getSecret(clientCard.getCashBoxId());
        dto.setCardId(clientCard.getId());
        dto.setCardNumber(clientCard.getCardNumber());
        dto.setToken(clientCard.getToken());
        dto.setParams(clientCard.getParams());
        dto.setSignature(DigestUtils.sha256Hex(clientCard.getId() + clientCard.getToken() + clientCard.getCardNumber() + secret));
        return dto;
    }


    public void sendTestP2p() {
        Payment payment = paymentService.generateSaveBankCardPayment();
        String p2pXml = halykSoapService.createP2pXml(payment.getPaySysPayId(), 663L,
                "900000028519", "900000028518", new BigDecimal("100.00"));
        LOGGER.info("p2pXml {}", p2pXml);
        String encodedXml = Base64.getEncoder().encodeToString(p2pXml.getBytes());
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://testpay.kkb.kz/jsp/hbpay/cid2cid.jsp?Signed_Order_B64=".concat(encodedXml),
                null, String.class);
        LOGGER.info("p2p response {}", response.getBody());
    }

    public ResultDTO sendAnonymousTestP2p() {
        Payment payment = paymentService.generateSaveBankCardPayment();
        String p2pXml = halykSoapService.createAnonymousP2pXml(payment.getPaySysPayId(), 663L,
                "900000028519", new BigDecimal("100.00"));
        LOGGER.info("p2pXml {}", p2pXml);

        String encodedXml = Base64.getEncoder().encodeToString(p2pXml.getBytes());
        Map<String, String> result = new HashMap<>();
        result.put("xml", encodedXml);
        result.put("backLink", "https://capitalpay.kz");
        result.put("postLink", "https://api.capitalpay.kz/api/p2p-link");
        return new ResultDTO(true, result, 0);
    }

}
