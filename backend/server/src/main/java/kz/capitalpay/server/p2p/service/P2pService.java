package kz.capitalpay.server.p2p.service;

import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.cashbox.service.CashboxCurrencyService;
import kz.capitalpay.server.cashbox.service.CashboxService;
import kz.capitalpay.server.constants.ErrorDictionary;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.p2p.dto.SendP2pToClientDto;
import kz.capitalpay.server.p2p.model.MerchantP2pSettings;
import kz.capitalpay.server.p2p.model.P2pPayment;
import kz.capitalpay.server.paysystems.systems.halyksoap.service.HalykSoapService;
import kz.capitalpay.server.usercard.dto.CardDataResponseDto;
import kz.capitalpay.server.usercard.model.UserCard;
import kz.capitalpay.server.usercard.service.UserCardService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import static kz.capitalpay.server.constants.ErrorDictionary.*;

@Service
public class P2pService {

    private static final Logger LOGGER = LoggerFactory.getLogger(P2pService.class);
    private final HalykSoapService halykSoapService;
    private final CashboxService cashboxService;
    private final UserCardService userCardService;
    private final P2pSettingsService p2pSettingsService;
    private final P2pPaymentService p2pPaymentService;
    private final CashboxCurrencyService cashboxCurrencyService;

    public P2pService(HalykSoapService halykSoapService, CashboxService cashboxService, UserCardService userCardService, P2pSettingsService p2pSettingsService, P2pPaymentService p2pPaymentService, CashboxCurrencyService cashboxCurrencyService) {
        this.halykSoapService = halykSoapService;
        this.cashboxService = cashboxService;
        this.userCardService = userCardService;
        this.p2pSettingsService = p2pSettingsService;
        this.p2pPaymentService = p2pPaymentService;
        this.cashboxCurrencyService = cashboxCurrencyService;
    }

    public ResultDTO sendP2pToClient(SendP2pToClientDto dto, String userAgent, String ipAddress) {
        if (checkP2pSignature(dto)) {
            return new ResultDTO(false, "Invalid signature", -1);
        }

        try {
            Cashbox cashbox = cashboxService.findById(dto.getCashBoxId());
            if (!cashbox.getMerchantId().equals(dto.getMerchantId())) {
                return ErrorDictionary.error122;
            }

            Long merchantCardId = cashboxService.findUserCardIdByCashBoxId(dto.getCashBoxId());
            if (merchantCardId.equals(0L)) {
                return ErrorDictionary.error130;
            }

            MerchantP2pSettings merchantP2pSettings = p2pSettingsService.findP2pSettingsByMerchantId(dto.getMerchantId());
            if (Objects.isNull(merchantP2pSettings) || !merchantP2pSettings.isP2pAllowed()) {
                return ErrorDictionary.error134;
            }

            if (!cashbox.isP2pAllowed()) {
                return ErrorDictionary.error134;
            }

            UserCard merchantCard = userCardService.findUserCardById(merchantCardId);
            CardDataResponseDto merchantCardData = userCardService.getCardDataFromTokenServer(merchantCard.getToken());
            CardDataResponseDto clientCardData = userCardService.getCardDataFromTokenServer(dto.getClientCardToken());

            boolean paymentSuccess = halykSoapService.sendP2p(ipAddress, userAgent, merchantCardData, dto, clientCardData.getCardNumber(), true);

            return paymentSuccess ? new ResultDTO(true, "Successful payment", 0) : ErrorDictionary.error135;
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorDictionary.error130;
        }
    }

    public ResultDTO sendP2pToMerchant(SendP2pToClientDto dto, String userAgent, String ipAddress) {
        if (checkP2pSignature(dto)) {
            return new ResultDTO(false, "Invalid signature", -1);
        }

        try {
            Cashbox cashbox = cashboxService.findById(dto.getCashBoxId());
            if (!cashbox.getMerchantId().equals(dto.getMerchantId())) {
                return ErrorDictionary.error122;
            }

            Long merchantCardId = cashboxService.findUserCardIdByCashBoxId(dto.getCashBoxId());
            if (merchantCardId.equals(0L)) {
                return ErrorDictionary.error130;
            }

            MerchantP2pSettings merchantP2pSettings = p2pSettingsService.findP2pSettingsByMerchantId(dto.getMerchantId());
            if (Objects.isNull(merchantP2pSettings) || !merchantP2pSettings.isP2pAllowed()) {
                return ErrorDictionary.error134;
            }

            if (!cashbox.isP2pAllowed()) {
                return ErrorDictionary.error134;
            }

            UserCard merchantCard = userCardService.findUserCardById(merchantCardId);
            CardDataResponseDto merchantCardData = userCardService.getCardDataFromTokenServer(merchantCard.getToken());
            CardDataResponseDto clientCardData = userCardService.getCardDataFromTokenServer(dto.getClientCardToken());

            boolean paymentSuccess = halykSoapService.sendP2p(ipAddress, userAgent, clientCardData, dto, merchantCardData.getCardNumber(), false);

            return paymentSuccess ? new ResultDTO(true, "Successful payment", 0) : ErrorDictionary.error135;
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorDictionary.error130;
        }
    }

    public ResultDTO createAnonymousP2pPayment(String userAgent, String ipAddress, Long cashBoxId, Long merchantId,
                                               BigDecimal totalAmount, String currency, String param, String signature) {
        if (checkAnonymousP2pSignature(cashBoxId, merchantId, totalAmount, signature)) {
            return new ResultDTO(false, "Invalid signature", -1);
        }

        Cashbox cashbox = cashboxService.findById(cashBoxId);
        if (cashbox == null) {
            return error113;
        }

        if (!cashbox.getMerchantId().equals(merchantId)) {
            return ErrorDictionary.error122;
        }
        BigDecimal amount = totalAmount.setScale(2, RoundingMode.HALF_UP);

        if (!cashboxCurrencyService.checkCurrencyEnable(cashbox.getId(), merchantId, currency)) {
            return error112;
        }

        if (param != null && param.length() > 255) {
            return error117;
        }

        P2pPayment p2pPayment = p2pPaymentService.generateP2pPayment(ipAddress, userAgent, merchantId, amount, cashBoxId, false, currency, param);
        return new ResultDTO(true, p2pPayment, 0);
    }

    private boolean checkP2pSignature(SendP2pToClientDto dto) {
        String secret = cashboxService.getSecret(dto.getCashBoxId());
        BigDecimal amount = dto.getAcceptedSum().setScale(2, RoundingMode.HALF_UP);
        String sha256hex = DigestUtils.sha256Hex(dto.getCashBoxId() + dto.getMerchantId() + dto.getClientCardToken() + amount.toString() + secret);
        LOGGER.info("Cashbox ID: {}", dto.getCashBoxId());
        LOGGER.info("Merchant ID: {}", dto.getMerchantId());
        LOGGER.info("Client card ID: {}", dto.getClientCardToken());
        LOGGER.info("amount.toString(): {}", amount.toString());
        LOGGER.info("Server sign: {}", sha256hex);
        LOGGER.info("Client sign: {}", dto.getSignature());
        return sha256hex.equals(dto.getSignature());
    }

    private boolean checkAnonymousP2pSignature(Long cashBoxId, Long merchantId, BigDecimal totalAmount, String signature) {
        String secret = cashboxService.getSecret(cashBoxId);
        BigDecimal amount = totalAmount.setScale(2, RoundingMode.HALF_UP);
        String sha256hex = DigestUtils.sha256Hex(cashBoxId + merchantId + amount.toString() + secret);
        LOGGER.info("Cashbox ID: {}", cashBoxId);
        LOGGER.info("Merchant ID: {}", merchantId);
        LOGGER.info("amount.toString(): {}", amount.toString());
        LOGGER.info("Server sign: {}", sha256hex);
        LOGGER.info("Client sign: {}", signature);
        return sha256hex.equals(signature);
    }
}
