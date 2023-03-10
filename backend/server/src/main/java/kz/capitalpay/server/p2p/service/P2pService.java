package kz.capitalpay.server.p2p.service;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.cashbox.service.CashboxCurrencyService;
import kz.capitalpay.server.cashbox.service.CashboxService;
import kz.capitalpay.server.constants.ErrorDictionary;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.merchantsettings.service.CashboxSettingsService;
import kz.capitalpay.server.merchantsettings.service.MerchantKycService;
import kz.capitalpay.server.p2p.dto.AnonymousP2pPaymentResponseDto;
import kz.capitalpay.server.p2p.dto.SendP2pToClientDto;
import kz.capitalpay.server.p2p.model.MerchantP2pSettings;
import kz.capitalpay.server.paymentlink.service.PaymentLinkService;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.payments.service.PaymentService;
import kz.capitalpay.server.paysystems.systems.halyksoap.dto.PaymentResultDto;
import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykAnonymousP2pOrder;
import kz.capitalpay.server.paysystems.systems.halyksoap.service.HalykSoapService;
import kz.capitalpay.server.terminal.model.Terminal;
import kz.capitalpay.server.terminal.repository.TerminalRepository;
import kz.capitalpay.server.usercard.dto.CardDataResponseDto;
import kz.capitalpay.server.usercard.model.ClientCardFromBank;
import kz.capitalpay.server.usercard.model.UserCard;
import kz.capitalpay.server.usercard.model.UserCardFromBank;
import kz.capitalpay.server.usercard.service.UserCardService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static kz.capitalpay.server.constants.ErrorDictionary.*;
import static kz.capitalpay.server.merchantsettings.service.CashboxSettingsService.REDIRECT_FAILED_URL;
import static kz.capitalpay.server.merchantsettings.service.CashboxSettingsService.REDIRECT_SUCCESS_URL;
import static kz.capitalpay.server.simple.service.SimpleService.SUCCESS;

@Service
public class P2pService {

    private static final Logger LOGGER = LoggerFactory.getLogger(P2pService.class);
    private final HalykSoapService halykSoapService;
    private final CashboxService cashboxService;
    private final UserCardService userCardService;
    private final P2pSettingsService p2pSettingsService;
    private final P2pPaymentService p2pPaymentService;
    private final CashboxCurrencyService cashboxCurrencyService;
    private final Gson gson;
    private final PaymentService paymentService;
    //    private final MerchantKycService merchantKycService;
    private final CashboxSettingsService cashboxSettingsService;
    //    private final HalykOrderRepository halykOrderRepository;
    private final TerminalRepository terminalRepository;
    private final PaymentLinkService paymentLinkService;
    private final MerchantKycService merchantKycService;

    @Value("${halyk.soap.p2p.termurl}")
    private String termUrl;

    @Value("${halyk.soap.currency}")
    private String currency;

    @Value("${remote.api.addres}")
    String apiAddress;


    public P2pService(HalykSoapService halykSoapService, CashboxService cashboxService, UserCardService userCardService, P2pSettingsService p2pSettingsService, P2pPaymentService p2pPaymentService, CashboxCurrencyService cashboxCurrencyService, Gson gson, PaymentService paymentService, CashboxSettingsService cashboxSettingsService, TerminalRepository terminalRepository, PaymentLinkService paymentLinkService, MerchantKycService merchantKycService) {
        this.halykSoapService = halykSoapService;
        this.cashboxService = cashboxService;
        this.userCardService = userCardService;
        this.p2pSettingsService = p2pSettingsService;
        this.p2pPaymentService = p2pPaymentService;
        this.cashboxCurrencyService = cashboxCurrencyService;
        this.gson = gson;
        this.paymentService = paymentService;
        this.cashboxSettingsService = cashboxSettingsService;
        this.terminalRepository = terminalRepository;
        this.paymentLinkService = paymentLinkService;
        this.merchantKycService = merchantKycService;
    }

    public RedirectView sendP2pToClient(SendP2pToClientDto dto, String userAgent, String ipAddress, RedirectAttributes redirectAttributes) {
        Map<String, String> resultUrls = cashboxSettingsService.getMerchantResultUrls(dto.getCashBoxId());
        if (!checkP2pSignature(dto)) {
            LOGGER.info(ErrorDictionary.INVALID_SIGNATURE.toString());
            addErrorAttributes(redirectAttributes, ErrorDictionary.INVALID_SIGNATURE);
            return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
        }

        try {
            Cashbox cashbox = cashboxService.findById(dto.getCashBoxId());
            if (!cashbox.getMerchantId().equals(dto.getMerchantId())) {
                LOGGER.info(ErrorDictionary.AVAILABLE_ONLY_FOR_CASHBOXS_OWNER.toString());
                addErrorAttributes(redirectAttributes, ErrorDictionary.AVAILABLE_ONLY_FOR_CASHBOXS_OWNER);
                return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
            }

            Long merchantCardId = cashboxService.findUserCardIdByCashBoxId(dto.getCashBoxId());
            if (merchantCardId.equals(0L)) {
                LOGGER.info(ErrorDictionary.CARD_NOT_FOUND.toString());
                addErrorAttributes(redirectAttributes, ErrorDictionary.CARD_NOT_FOUND);
                return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
            }

            MerchantP2pSettings merchantP2pSettings = p2pSettingsService.findP2pSettingsByMerchantId(dto.getMerchantId());
            if (Objects.isNull(merchantP2pSettings) || !merchantP2pSettings.isP2pAllowed()) {
                LOGGER.info(ErrorDictionary.P2P_IS_NOT_ALLOWED.toString());
                addErrorAttributes(redirectAttributes, ErrorDictionary.P2P_IS_NOT_ALLOWED);
                return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
            }

            if (!cashbox.isP2pAllowed()) {
                LOGGER.info(ErrorDictionary.P2P_IS_NOT_ALLOWED.toString());
                addErrorAttributes(redirectAttributes, ErrorDictionary.P2P_IS_NOT_ALLOWED);
                return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
            }
            if (Objects.isNull(merchantP2pSettings.getTerminalId())) {
                LOGGER.info(MERCHANT_TERMINAL_SETTINGS_NOT_FOUND.toString());
                addErrorAttributes(redirectAttributes, ErrorDictionary.MERCHANT_TERMINAL_SETTINGS_NOT_FOUND);
                return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
            }
            Terminal terminal = terminalRepository.findByIdAndDeletedFalse(merchantP2pSettings.getTerminalId()).orElse(null);
            if (Objects.isNull(terminal)) {
                LOGGER.info(MERCHANT_TERMINAL_SETTINGS_NOT_FOUND.toString());
                addErrorAttributes(redirectAttributes, ErrorDictionary.MERCHANT_TERMINAL_SETTINGS_NOT_FOUND);
                return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
            }

            UserCard merchantCard = userCardService.findUserCardById(merchantCardId);
            CardDataResponseDto merchantCardData = userCardService.getCardDataFromTokenServer(merchantCard.getToken());
            CardDataResponseDto clientCardData = userCardService.getCardDataFromTokenServer(dto.getClientCardToken());
            String merchantName = merchantKycService.getField(dto.getMerchantId(), MerchantKycService.MNAME);

            return checkReturnCode(halykSoapService.sendP2p(ipAddress, userAgent, merchantCardData, dto,
                    clientCardData.getCardNumber(), true, terminal.getOutputTerminalId(), merchantName, cashbox.getName()),
                    resultUrls, redirectAttributes);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info(ErrorDictionary.CARD_NOT_FOUND.toString());
            addErrorAttributes(redirectAttributes, ErrorDictionary.CARD_NOT_FOUND);
            return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
        }
    }

    public RedirectView sendBankP2pToClient(SendP2pToClientDto dto, String userAgent, String ipAddress, RedirectAttributes redirectAttributes) {
        Map<String, String> resultUrls = cashboxSettingsService.getMerchantResultUrls(dto.getCashBoxId());
        if (!checkP2pSignature(dto)) {
            LOGGER.info(ErrorDictionary.INVALID_SIGNATURE.toString());
            addErrorAttributes(redirectAttributes, ErrorDictionary.INVALID_SIGNATURE);
            return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
        }

        try {
            Cashbox cashbox = cashboxService.findById(dto.getCashBoxId());
            if (!cashbox.getMerchantId().equals(dto.getMerchantId())) {
                LOGGER.info(ErrorDictionary.AVAILABLE_ONLY_FOR_CASHBOXS_OWNER.toString());
                addErrorAttributes(redirectAttributes, ErrorDictionary.AVAILABLE_ONLY_FOR_CASHBOXS_OWNER);
                return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
            }

            Long merchantCardId = cashboxService.findUserCardIdByCashBoxId(dto.getCashBoxId());
            if (merchantCardId.equals(0L)) {
                LOGGER.info(ErrorDictionary.CARD_NOT_FOUND.toString());
                addErrorAttributes(redirectAttributes, ErrorDictionary.CARD_NOT_FOUND);
                return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
            }

            MerchantP2pSettings merchantP2pSettings = p2pSettingsService.findP2pSettingsByMerchantId(dto.getMerchantId());
            if (Objects.isNull(merchantP2pSettings) || !merchantP2pSettings.isP2pAllowed()) {
                LOGGER.info(ErrorDictionary.P2P_IS_NOT_ALLOWED.toString());
                addErrorAttributes(redirectAttributes, ErrorDictionary.P2P_IS_NOT_ALLOWED);
                return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
            }

            if (!cashbox.isP2pAllowed()) {
                LOGGER.info(ErrorDictionary.P2P_IS_NOT_ALLOWED.toString());
                addErrorAttributes(redirectAttributes, ErrorDictionary.P2P_IS_NOT_ALLOWED);
                return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
            }
            if (Objects.isNull(merchantP2pSettings.getTerminalId())) {
                LOGGER.info(MERCHANT_TERMINAL_SETTINGS_NOT_FOUND.toString());
                addErrorAttributes(redirectAttributes, ErrorDictionary.MERCHANT_TERMINAL_SETTINGS_NOT_FOUND);
                return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
            }
            Terminal terminal = terminalRepository.findByIdAndDeletedFalse(merchantP2pSettings.getTerminalId()).orElse(null);
            if (Objects.isNull(terminal)) {
                LOGGER.info(MERCHANT_TERMINAL_SETTINGS_NOT_FOUND.toString());
                addErrorAttributes(redirectAttributes, ErrorDictionary.MERCHANT_TERMINAL_SETTINGS_NOT_FOUND);
                return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
            }

            UserCardFromBank userCard = userCardService.findUserCardFromBankById(merchantCardId);
            ClientCardFromBank clientCard = userCardService.findClientCardFromBankByToken(dto.getClientCardToken());
            String merchantName = merchantKycService.getField(dto.getMerchantId(), MerchantKycService.MNAME);

            Payment payment = p2pPaymentService.generateP2pPayment(ipAddress, userAgent, dto.getMerchantId(),
                    dto.getAcceptedSum(), dto.getCashBoxId(), true, currency, dto.getParam(),
                    terminal.getOutputTerminalId(), merchantName, cashbox.getName());
            setPaymentUsersData(payment, userCard.getCardNumber(), userCard.getPhone(), userCard.getEmail(), userCard.getName(), clientCard.getCardNumber(), clientCard.getPhone(), clientCard.getEmail(), clientCard.getName());
            return checkReturnCode(halykSoapService.sendSavedCardsP2p(userCard.getBankCardId(), dto, clientCard.getBankCardId(), payment, terminal.getOutputTerminalId()), resultUrls, redirectAttributes);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info(ErrorDictionary.CARD_NOT_FOUND.toString());
            addErrorAttributes(redirectAttributes, ErrorDictionary.CARD_NOT_FOUND);
            return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
        }
    }

    public RedirectView sendP2pToMerchant(SendP2pToClientDto dto, String userAgent, String ipAddress, RedirectAttributes redirectAttributes) {
        Map<String, String> resultUrls = cashboxSettingsService.getMerchantResultUrls(dto.getCashBoxId());
        if (!checkP2pSignature(dto)) {
            LOGGER.info(new ResultDTO(false, "Invalid signature", -1).toString());
            addErrorAttributes(redirectAttributes, ErrorDictionary.INVALID_SIGNATURE);
            return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
        }

        try {
            Cashbox cashbox = cashboxService.findById(dto.getCashBoxId());
            if (!cashbox.getMerchantId().equals(dto.getMerchantId())) {
                LOGGER.info(ErrorDictionary.AVAILABLE_ONLY_FOR_CASHBOXS_OWNER.toString());
                addErrorAttributes(redirectAttributes, ErrorDictionary.AVAILABLE_ONLY_FOR_CASHBOXS_OWNER);
                return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
            }
            Long merchantCardId = cashboxService.findUserCardIdByCashBoxId(dto.getCashBoxId());
            if (merchantCardId.equals(0L)) {
                LOGGER.info(ErrorDictionary.CARD_NOT_FOUND.toString());
                addErrorAttributes(redirectAttributes, ErrorDictionary.CARD_NOT_FOUND);
                return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
            }
            MerchantP2pSettings merchantP2pSettings = p2pSettingsService.findP2pSettingsByMerchantId(dto.getMerchantId());
            if (Objects.isNull(merchantP2pSettings) || !merchantP2pSettings.isP2pAllowed()) {
                LOGGER.info(ErrorDictionary.P2P_IS_NOT_ALLOWED.toString());
                addErrorAttributes(redirectAttributes, ErrorDictionary.P2P_IS_NOT_ALLOWED);
                return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
            }
            if (!cashbox.isP2pAllowed()) {
                LOGGER.info(ErrorDictionary.P2P_IS_NOT_ALLOWED.toString());
                addErrorAttributes(redirectAttributes, ErrorDictionary.P2P_IS_NOT_ALLOWED);
                return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
            }
            if (Objects.isNull(merchantP2pSettings.getTerminalId())) {
                LOGGER.info(MERCHANT_TERMINAL_SETTINGS_NOT_FOUND.toString());
                addErrorAttributes(redirectAttributes, ErrorDictionary.MERCHANT_TERMINAL_SETTINGS_NOT_FOUND);
                return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
            }
            Terminal terminal = terminalRepository.findByIdAndDeletedFalse(merchantP2pSettings.getTerminalId()).orElse(null);
            if (Objects.isNull(terminal)) {
                LOGGER.info(MERCHANT_TERMINAL_SETTINGS_NOT_FOUND.toString());
                addErrorAttributes(redirectAttributes, ErrorDictionary.MERCHANT_TERMINAL_SETTINGS_NOT_FOUND);
                return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
            }
            UserCard merchantCard = userCardService.findUserCardById(merchantCardId);
            CardDataResponseDto merchantCardData = userCardService.getCardDataFromTokenServer(merchantCard.getToken());
            CardDataResponseDto clientCardData = userCardService.getCardDataFromTokenServer(dto.getClientCardToken());
            String merchantName = merchantKycService.getField(dto.getMerchantId(), MerchantKycService.MNAME);

            return checkReturnCode(halykSoapService.sendP2p(ipAddress, userAgent, clientCardData, dto, merchantCardData.getCardNumber(), false, terminal.getInputTerminalId(), merchantName, cashbox.getName()), resultUrls, redirectAttributes);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info(ErrorDictionary.CARD_NOT_FOUND.toString());
            addErrorAttributes(redirectAttributes, ErrorDictionary.CARD_NOT_FOUND);
            return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
        }
    }

    public RedirectView sendBankP2pToMerchant(SendP2pToClientDto dto, String userAgent, String ipAddress, RedirectAttributes redirectAttributes) {
        Map<String, String> resultUrls = cashboxSettingsService.getMerchantResultUrls(dto.getCashBoxId());
        if (!checkP2pSignature(dto)) {
            LOGGER.info(new ResultDTO(false, "Invalid signature", -1).toString());
            addErrorAttributes(redirectAttributes, ErrorDictionary.INVALID_SIGNATURE);
            return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
        }

        try {
            Cashbox cashbox = cashboxService.findById(dto.getCashBoxId());
            if (!cashbox.getMerchantId().equals(dto.getMerchantId())) {
                LOGGER.info(ErrorDictionary.AVAILABLE_ONLY_FOR_CASHBOXS_OWNER.toString());
                addErrorAttributes(redirectAttributes, ErrorDictionary.AVAILABLE_ONLY_FOR_CASHBOXS_OWNER);
                return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
            }
            Long merchantCardId = cashboxService.findUserCardIdByCashBoxId(dto.getCashBoxId());
            if (merchantCardId.equals(0L)) {
                LOGGER.info(ErrorDictionary.CARD_NOT_FOUND.toString());
                addErrorAttributes(redirectAttributes, ErrorDictionary.CARD_NOT_FOUND);
                return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
            }
            MerchantP2pSettings merchantP2pSettings = p2pSettingsService.findP2pSettingsByMerchantId(dto.getMerchantId());
            if (Objects.isNull(merchantP2pSettings) || !merchantP2pSettings.isP2pAllowed()) {
                LOGGER.info(ErrorDictionary.P2P_IS_NOT_ALLOWED.toString());
                addErrorAttributes(redirectAttributes, ErrorDictionary.P2P_IS_NOT_ALLOWED);
                return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
            }
            if (!cashbox.isP2pAllowed()) {
                LOGGER.info(ErrorDictionary.P2P_IS_NOT_ALLOWED.toString());
                addErrorAttributes(redirectAttributes, ErrorDictionary.P2P_IS_NOT_ALLOWED);
                return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
            }
            if (Objects.isNull(merchantP2pSettings.getTerminalId())) {
                LOGGER.info(MERCHANT_TERMINAL_SETTINGS_NOT_FOUND.toString());
                addErrorAttributes(redirectAttributes, ErrorDictionary.MERCHANT_TERMINAL_SETTINGS_NOT_FOUND);
                return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
            }
            Terminal terminal = terminalRepository.findByIdAndDeletedFalse(merchantP2pSettings.getTerminalId()).orElse(null);
            if (Objects.isNull(terminal)) {
                LOGGER.info(MERCHANT_TERMINAL_SETTINGS_NOT_FOUND.toString());
                addErrorAttributes(redirectAttributes, ErrorDictionary.MERCHANT_TERMINAL_SETTINGS_NOT_FOUND);
                return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
            }
            UserCardFromBank userCard = userCardService.findUserCardFromBankById(merchantCardId);
            ClientCardFromBank clientCard = userCardService.findClientCardFromBankByToken(dto.getClientCardToken());
            String merchantName = merchantKycService.getField(dto.getMerchantId(), MerchantKycService.MNAME);

            Payment payment = p2pPaymentService.generateP2pPayment(ipAddress, userAgent, dto.getMerchantId(),
                    dto.getAcceptedSum(), dto.getCashBoxId(), false, currency, dto.getParam(),
                    terminal.getOutputTerminalId(), merchantName, cashbox.getName());
            setPaymentUsersData(payment, clientCard.getCardNumber(), clientCard.getPhone(), clientCard.getEmail(), clientCard.getName(), userCard.getCardNumber(), userCard.getPhone(), userCard.getEmail(), userCard.getName());
            return checkReturnCode(halykSoapService.sendSavedCardsP2p(clientCard.getBankCardId(), dto, userCard.getBankCardId(), payment, terminal.getOutputTerminalId()), resultUrls, redirectAttributes);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info(ErrorDictionary.CARD_NOT_FOUND.toString());
            addErrorAttributes(redirectAttributes, ErrorDictionary.CARD_NOT_FOUND);
            return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
        }
    }

    public ResultDTO createAnonymousP2pPayment(String userAgent, String ipAddress, Long cashBoxId, Long merchantId, BigDecimal totalAmount, String currency, String param, String signature) {
        if (!signature.equals("testSignature") && !checkAnonymousP2pSignature(cashBoxId, merchantId, totalAmount, signature)) {
            LOGGER.info("INVALID_SIGNATURE");
            return ErrorDictionary.INVALID_SIGNATURE;
        }
        Cashbox cashbox = cashboxService.findById(cashBoxId);
        if (cashbox == null) {
            LOGGER.info("CASHBOX_NOT_FOUND");
            return CASHBOX_NOT_FOUND;
        }
        if (!cashbox.getMerchantId().equals(merchantId)) {
            LOGGER.info("AVAILABLE_ONLY_FOR_CASHBOXES");
            return ErrorDictionary.AVAILABLE_ONLY_FOR_CASHBOXS_OWNER;
        }
        BigDecimal amount = totalAmount.setScale(2, RoundingMode.HALF_UP);
        if (!cashboxCurrencyService.checkCurrencyEnable(cashbox.getId(), merchantId, currency)) {
            LOGGER.info("CURRENCY_NOT_FOUND");
            return CURRENCY_NOT_FOUND;
        }
        if (param != null && param.length() > 255) {
            LOGGER.info("PARAM_IS_TOO_LONG");
            return PARAM_IS_TOO_LONG;
        }
        String merchantName = merchantKycService.getField(merchantId, MerchantKycService.MNAME);
        Payment p2pPayment = p2pPaymentService.generateP2pPayment(ipAddress, userAgent, merchantId, amount, cashBoxId,
                false, currency, param, null, merchantName, cashbox.getName());
        return new ResultDTO(true, p2pPayment, 0);
    }

    public ResultDTO sendAnonymousP2pPayment(HttpServletRequest httpRequest, String paymentId, String cardHolderName, String cvv, String month, String pan, String year) {
        String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = httpRequest.getRemoteAddr();
        }
        LOGGER.info("Payment ID: {}", paymentId);
        LOGGER.info("Request IP: {}", ipAddress);
        LOGGER.info("Request User-Agent: {}", httpRequest.getHeader("User-Agent"));

        Payment payment = p2pPaymentService.findById(paymentId);

//        String result = halykSoapService.getPaymentOrderResult(payment.getTotalAmount(),
//                cardHolderName, cvv, "P2p payment to merchant", month, payment.getPaySysPayId(), pan, year);

        try {
            Cashbox cashbox = cashboxService.findById(payment.getCashboxId());
            if (!cashbox.getMerchantId().equals(payment.getMerchantId())) {
                return ErrorDictionary.AVAILABLE_ONLY_FOR_CASHBOXS_OWNER;
            }
            Long merchantCardId = cashboxService.findUserCardIdByCashBoxId(payment.getCashboxId());
            if (merchantCardId.equals(0L)) {
                return ErrorDictionary.CARD_NOT_FOUND;
            }
            MerchantP2pSettings merchantP2pSettings = p2pSettingsService.findP2pSettingsByMerchantId(payment.getMerchantId());
            if (Objects.isNull(merchantP2pSettings) || !merchantP2pSettings.isP2pAllowed()) {
                return ErrorDictionary.P2P_IS_NOT_ALLOWED;
            }
            if (!cashbox.isP2pAllowed()) {
                return ErrorDictionary.P2P_IS_NOT_ALLOWED;
            }
            if (Objects.isNull(merchantP2pSettings.getTerminalId())) {
                LOGGER.info(MERCHANT_TERMINAL_SETTINGS_NOT_FOUND.toString());
                return MERCHANT_TERMINAL_SETTINGS_NOT_FOUND;
            }
            Terminal terminal = terminalRepository.findByIdAndDeletedFalse(merchantP2pSettings.getTerminalId()).orElse(null);
            if (Objects.isNull(terminal)) {
                LOGGER.info(MERCHANT_TERMINAL_SETTINGS_NOT_FOUND.toString());
                return MERCHANT_TERMINAL_SETTINGS_NOT_FOUND;
            }
            UserCard merchantCard = userCardService.findUserCardById(merchantCardId);
            CardDataResponseDto merchantCardData = userCardService.getCardDataFromTokenServer(merchantCard.getToken());
            CardDataResponseDto clientCardData = new CardDataResponseDto(pan, year, month, cvv);
            SendP2pToClientDto dto = new SendP2pToClientDto(payment.getMerchantId(), payment.getTotalAmount(), payment.getCashboxId());
            String merchantName = merchantKycService.getField(dto.getMerchantId(), MerchantKycService.MNAME);
            return checkReturnCode(halykSoapService.sendP2p(ipAddress, payment.getUserAgent(), clientCardData, dto, merchantCardData.getCardNumber(), false, terminal.getInputTerminalId(), merchantName, cashbox.getName()));
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorDictionary.CARD_NOT_FOUND;
        }
    }

    public ResultDTO sendBankAnonymousP2pPayment(HttpServletRequest httpRequest, Payment payment) {
        String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = httpRequest.getRemoteAddr();
        }
        LOGGER.info("Payment ID: {}", payment.getGuid());
        LOGGER.info("Request IP: {}", ipAddress);
        LOGGER.info("Request User-Agent: {}", httpRequest.getHeader("User-Agent"));

        try {
            Cashbox cashbox = cashboxService.findById(payment.getCashboxId());
            if (!cashbox.getMerchantId().equals(payment.getMerchantId())) {
                return ErrorDictionary.AVAILABLE_ONLY_FOR_CASHBOXS_OWNER;
            }
            Long merchantCardId = cashboxService.findUserCardIdByCashBoxId(payment.getCashboxId());
            if (merchantCardId.equals(0L)) {
                return ErrorDictionary.CARD_NOT_FOUND;
            }
            MerchantP2pSettings merchantP2pSettings = p2pSettingsService.findP2pSettingsByMerchantId(payment.getMerchantId());
            if (Objects.isNull(merchantP2pSettings) || !merchantP2pSettings.isP2pAllowed()) {
                return ErrorDictionary.P2P_IS_NOT_ALLOWED;
            }
            if (!cashbox.isP2pAllowed()) {
                return ErrorDictionary.P2P_IS_NOT_ALLOWED;
            }
            if (Objects.isNull(merchantP2pSettings.getTerminalId())) {
                LOGGER.info(MERCHANT_TERMINAL_SETTINGS_NOT_FOUND.toString());
                return MERCHANT_TERMINAL_SETTINGS_NOT_FOUND;
            }
            Terminal terminal = terminalRepository.findByIdAndDeletedFalse(merchantP2pSettings.getTerminalId()).orElse(null);
            if (Objects.isNull(terminal)) {
                LOGGER.info(MERCHANT_TERMINAL_SETTINGS_NOT_FOUND.toString());
                return MERCHANT_TERMINAL_SETTINGS_NOT_FOUND;
            }
            UserCardFromBank merchantCard = userCardService.findUserCardFromBankById(merchantCardId);
            String p2pXml = halykSoapService.createAnonymousP2pXml(payment.getPaySysPayId(), payment.getMerchantId(), merchantCard.getBankCardId(), payment.getTotalAmount(), terminal.getInputTerminalId());
            LOGGER.info("p2pXml {}", p2pXml);

            setPaymentReceiverData(payment, merchantCard.getCardNumber(), merchantCard.getPhone(), merchantCard.getEmail(), merchantCard.getName());

            Map<String, String> resultUrls = cashboxSettingsService.getMerchantResultUrls(cashbox.getId());
            String encodedXml = Base64.getEncoder().encodeToString(p2pXml.getBytes());
            Map<String, String> result = new HashMap<>();
            result.put("xml", encodedXml);
            if (Objects.nonNull(resultUrls)) {
                result.put("backLink", resultUrls.get(REDIRECT_SUCCESS_URL));
            }
            result.put("postLink", apiAddress + "/api/p2p-link");
            result.put("terminalId", terminal.getInputTerminalId().toString());
            return new ResultDTO(true, result, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorDictionary.CARD_NOT_FOUND;
        }
    }

    public void completeBankAnonymousP2p(String requestBody) {
        LOGGER.info("completeBankAnonymousP2p()");
        if (Objects.isNull(requestBody) || requestBody.trim().isEmpty()) {
            LOGGER.info("requestBody is NULL");
            return;
        }
        String xml = requestBody.replace("response=", "");
        HalykAnonymousP2pOrder halykAnonymousP2pOrder = halykSoapService.parseBankAnonymousP2p(xml);
        if (Objects.isNull(halykAnonymousP2pOrder)) {
            LOGGER.info("halykAnonymousP2pOrder is NULL");
            return;
        }
        if (Objects.nonNull(halykAnonymousP2pOrder.getResponseCode()) && halykAnonymousP2pOrder.getResponseCode().equals("00")) {
            paymentService.setStatusByPaySysPayId(halykAnonymousP2pOrder.getOrderId(), SUCCESS, null);
            Payment payment = paymentService.findByPaySysPayId(halykAnonymousP2pOrder.getOrderId());
            payment.setRrn(halykAnonymousP2pOrder.getReference());
            payment.setPayerEmail(halykAnonymousP2pOrder.getEmail());
            payment.setPayerName(halykAnonymousP2pOrder.getBankName());
            payment.setPayerPhone(halykAnonymousP2pOrder.getPhone());
            String payerPan = Objects.nonNull(halykAnonymousP2pOrder.getCardHash()) ? halykAnonymousP2pOrder.getCardHash().replace("-", "").replace("X", "*") : null;
            payment.setPayerPan(payerPan);
            paymentService.save(payment);
            if (Objects.nonNull(payment.getPaymentLinkId())) {
                paymentLinkService.disablePaymentLink(payment.getPaymentLinkId());
            }
        }
    }

    private boolean checkP2pSignature(SendP2pToClientDto dto) {
        LOGGER.info("checkP2pSignature");
        String secret = cashboxService.getSecret(dto.getCashBoxId());
        BigDecimal amount = dto.getAcceptedSum().setScale(2, RoundingMode.HALF_UP);
        String sha256hex = DigestUtils.sha256Hex(dto.getCashBoxId().toString() + dto.getMerchantId().toString() + dto.getClientCardToken() + amount.toString() + secret);
        LOGGER.info("Sign data: {}", dto.getCashBoxId() + dto.getMerchantId() + dto.getClientCardToken() + amount.toString() + secret);
        LOGGER.info("Cashbox ID: {}", dto.getCashBoxId());
        LOGGER.info("Merchant ID: {}", dto.getMerchantId());
        LOGGER.info("Client card ID: {}", dto.getClientCardToken());
        LOGGER.info("amount.toString(): {}", amount.toString());
        LOGGER.info("Server sign: {}", sha256hex);
        LOGGER.info("Client sign: {}", dto.getSignature());
        return sha256hex.equals(dto.getSignature());
    }

    private boolean checkAnonymousP2pSignature(Long cashBoxId, Long merchantId, BigDecimal totalAmount, String signature) {
        LOGGER.info("checkAnonymousP2pSignature");
        String secret = cashboxService.getSecret(cashBoxId);
        BigDecimal amount = totalAmount.setScale(2, RoundingMode.HALF_UP);
        String sha256hex = DigestUtils.sha256Hex(cashBoxId.toString() + merchantId.toString() + amount.toString() + secret);
        LOGGER.info("Cashbox ID: {}", cashBoxId);
        LOGGER.info("Merchant ID: {}", merchantId);
        LOGGER.info("amount.toString(): {}", amount.toString());
        LOGGER.info("Server sign: {}", sha256hex);
        LOGGER.info("Client sign: {}", signature);
        return sha256hex.equals(signature);
    }

    private ResultDTO checkReturnCode(PaymentResultDto paymentResult) {
        if (paymentResult.getResultDTO().isResult() && !paymentResult.isIs3ds()) {
            return new ResultDTO(true, "Successful payment", 0);
        }
        if (!paymentResult.getResultDTO().isResult()) {
            return paymentResult.getResultDTO();
        }
        LOGGER.info("Redirect to 3DS");
        LOGGER.info("Result: {}", paymentResult);
        try {
            LinkedHashMap<String, String> param = gson.fromJson(paymentResult.getStrFor3ds(), LinkedHashMap.class);
            AnonymousP2pPaymentResponseDto dto = new AnonymousP2pPaymentResponseDto(param.get("acsUrl"), param.get("MD"), param.get("PaReq"), termUrl, true);
            return new ResultDTO(true, dto, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return BANK_ERROR;
        }
    }

    private RedirectView checkReturnCode(PaymentResultDto paymentResult, Map<String, String> resultUrls, RedirectAttributes redirectAttributes) {
        if (paymentResult.getResultDTO().isResult() && !paymentResult.isIs3ds()) {
            return new RedirectView(resultUrls.get(REDIRECT_SUCCESS_URL));
        }
        if (!paymentResult.getResultDTO().isResult()) {
            addErrorAttributes(redirectAttributes, paymentResult.getResultDTO());
            return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
        }
        LOGGER.info("Redirect to 3DS");
        LOGGER.info("Result: {}", paymentResult);
        try {
            String secureRedirectUrl = apiAddress + "/public/paysystem/secure/redirect";
            LinkedHashMap<String, String> param = gson.fromJson(paymentResult.getStrFor3ds(), LinkedHashMap.class);
            redirectAttributes.addAttribute("acsUrl", param.get("acsUrl"));
            redirectAttributes.addAttribute("MD", param.get("MD"));
            redirectAttributes.addAttribute("PaReq", param.get("PaReq"));
            return new RedirectView(secureRedirectUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
        }
    }

    private RedirectView checkReturnCode(ResultDTO paymentResult, Map<String, String> resultUrls, RedirectAttributes redirectAttributes) {
        if (paymentResult.isResult()) {
            return new RedirectView(resultUrls.get(REDIRECT_SUCCESS_URL));
        } else {
            addErrorAttributes(redirectAttributes, ErrorDictionary.BANK_ERROR);
            return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
        }
    }

    private void addErrorAttributes(RedirectAttributes redirectAttributes, ResultDTO error) {
        redirectAttributes.addAttribute("errorCode", error.getError());
        redirectAttributes.addAttribute("errorDescription", error.getData());
    }

    private void setPaymentUsersData(Payment payment, String payerPan, String payerPhone, String payerEmail, String payerName, String receiverPan, String receiverPhone, String receiverEmail, String receiverName) {
        payment.setPayerPan(payerPan);
        payment.setPayerPhone(payerPhone);
        payment.setPayerName(payerName);
        payment.setPayerEmail(payerEmail);
        payment.setReceiverPan(receiverPan);
        payment.setReceiverEmail(receiverEmail);
        payment.setReceiverPhone(receiverPhone);
        payment.setReceiverName(receiverName);
        paymentService.save(payment);
    }

    private void setPaymentReceiverData(Payment payment, String receiverPan, String receiverPhone, String receiverEmail, String receiverName) {
        payment.setReceiverPan(receiverPan);
        payment.setReceiverEmail(receiverEmail);
        payment.setReceiverPhone(receiverPhone);
        payment.setReceiverName(receiverName);
        paymentService.save(payment);
    }

//    private BillPaymentDto createBill(P2pPayment payment, HttpServletRequest httpRequest, String cardHolderName, String pan, String result) {
//        BillPaymentDto billPaymentDto = new BillPaymentDto();
//        billPaymentDto.setResultPayment(result);
//        setAmountFields(payment.getCashboxId(), payment.getTotalAmount(), billPaymentDto, payment.getCurrency());
//        billPaymentDto.setNumberTransaction(payment.getOrderId());
//        if (!"ok".equalsIgnoreCase(result)) {
//            return billPaymentDto;
//        }
//        billPaymentDto.setWebSiteMerchant(httpRequest.getServerName());
//        billPaymentDto.setDateTransaction(payment.getLocalDateTime());
//        billPaymentDto.setTypeTransaction(1);
//        billPaymentDto.setCardHolderName(cardHolderName);
//        billPaymentDto.setCardNumber(pan);
//        billPaymentDto.setPaySystemName(pan);
//        return billPaymentDto;
//    }

//    private void setAmountFields(Long cashboxId, BigDecimal totalAmount, BillPaymentDto billPaymentDto, String currency) {
//        BigDecimal oneHundred = new BigDecimal(100);
//        Cashbox cashbox = cashboxService.findById(cashboxId);
//        BigDecimal totalFee = BigDecimal.valueOf(Long.parseLong(merchantKycService.getField(cashbox.getMerchantId(),
//                TOTAL_FEE)));
//        BigDecimal clientFee = BigDecimal.valueOf(Long.parseLong(cashboxSettingsService
//                .getField(cashboxId, CLIENT_FEE)));
//
//        BigDecimal amountWithoutClientFee = totalAmount
//                .divide((BigDecimal.ONE
//                                .subtract(clientFee
//                                        .divide(oneHundred, MathContext.DECIMAL128))
//                                .divide(BigDecimal.ONE
//                                                .subtract(totalFee
//                                                        .divide(oneHundred, MathContext.DECIMAL128)),
//                                        MathContext.DECIMAL128)),
//                        MathContext.DECIMAL128)
//                .setScale(0, RoundingMode.HALF_UP);
//        billPaymentDto.setTotalAmount(totalAmount.toString(), currency);
//        billPaymentDto.setAmountPayment(amountWithoutClientFee.toString(), currency);
//        billPaymentDto.setAmountFee(totalAmount.subtract(amountWithoutClientFee).toString(), currency);
//    }
}
