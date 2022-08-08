package kz.capitalpay.server.cashbox.service;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.dto.*;
import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.cashbox.repository.CashboxRepository;
import kz.capitalpay.server.constants.ErrorDictionary;
import kz.capitalpay.server.currency.dto.MerchantRequestDTO;
import kz.capitalpay.server.currency.service.CurrencyService;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationRoleService;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.merchantsettings.service.CashboxSettingsService;
import kz.capitalpay.server.p2p.model.MerchantP2pSettings;
import kz.capitalpay.server.p2p.service.P2pSettingsService;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.payments.repository.PaymentRepository;
import kz.capitalpay.server.payments.service.PaymentService;
import kz.capitalpay.server.usercard.model.UserCard;
import kz.capitalpay.server.usercard.model.UserCardFromBank;
import kz.capitalpay.server.usercard.repository.UserBankCardRepository;
import kz.capitalpay.server.usercard.repository.UserCardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static kz.capitalpay.server.constants.ErrorDictionary.*;
import static kz.capitalpay.server.merchantsettings.service.CashboxSettingsService.*;
import static kz.capitalpay.server.simple.service.SimpleService.FAILED;
import static kz.capitalpay.server.simple.service.SimpleService.SUCCESS;

@Service
public class CashboxService {

    Logger logger = LoggerFactory.getLogger(CashboxService.class);

    Random random = new Random();

    @Autowired
    Gson gson;

    @Autowired
    ApplicationUserService applicationUserService;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    CashboxSettingsService cashboxSettingsService;

    @Autowired
    CashboxRepository cashboxRepository;

    @Autowired
    ApplicationRoleService applicationRoleService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    UserCardRepository userCardRepository;

    @Autowired
    P2pSettingsService p2pSettingsService;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    UserBankCardRepository userBankCardRepository;

    public ResultDTO createNew(Principal principal, CashboxCreateRequestDTO request) {
        try {
            ApplicationUser owner = applicationUserService.getUserByLogin(principal.getName());
            Cashbox cashbox = new Cashbox();
            cashbox.setMerchantId(owner.getId());
            cashbox.setName(request.getName());
            cashboxRepository.save(cashbox);

            if (currencyService.checkEnable(request.getCurrency())) {
                cashboxSettingsService.setField(cashbox.getId(), CASHBOX_CURRENCY_LIST, gson.toJson(List.of(request.getCurrency())));
            } else {
                cashboxRepository.delete(cashbox);
                return CURRENCY_NOT_FOUND;
            }
            return new ResultDTO(true, cashbox, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO changeName(Principal principal, CashboxNameRequestDTO request) {
        try {
            ApplicationUser owner = applicationUserService.getUserByLogin(principal.getName());
            Cashbox cashbox = cashboxRepository.findById(request.getCashboxId()).orElse(null);
            if (cashbox == null) {
                return CASHBOX_NOT_FOUND;
            }
            if (!cashbox.getMerchantId().equals(owner.getId())) {
                return NOT_ENOUGH_RIGHTS;
            }
            cashbox.setName(request.getName());
            cashboxRepository.save(cashbox);
            return new ResultDTO(true, cashbox, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO delete(Principal principal, CashboxRequestDTO request) {
        try {
            ApplicationUser owner = applicationUserService.getUserByLogin(principal.getName());
            Cashbox cashbox = cashboxRepository.findById(request.getCashboxId()).orElse(null);
            if (cashbox == null) {
                return CASHBOX_NOT_FOUND;
            }
            if (!cashbox.getMerchantId().equals(owner.getId())) {
                return NOT_ENOUGH_RIGHTS;
            }
            cashbox.setDeleted(true);
            cashboxRepository.save(cashbox);
            List<Cashbox> cashboxList = cashboxRepository.findByMerchantIdAndDeletedFalse(owner.getId());
            return new ResultDTO(true, cashboxList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO list(Principal principal, MerchantRequestDTO request) {
        try {
            ApplicationUser owner = applicationUserService.getUserByLogin(principal.getName());
            List<Cashbox> cashboxList = cashboxRepository.findByMerchantIdAndDeletedFalse(owner.getId());
            return new ResultDTO(true, cashboxList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public Cashbox findById(Long cashboxid) {
        return cashboxRepository.findById(cashboxid).orElse(null);
    }

    public Long findUserCardIdByCashBoxId(Long cashBoxId) {
        Cashbox cashbox = cashboxRepository.findById(cashBoxId).orElse(null);
        if (Objects.isNull(cashbox)) {
            return 0L;
        }
        if (!cashbox.isUseDefaultCard()) {
            return cashboxRepository.findCardById(cashBoxId);
        }
        MerchantP2pSettings merchantP2pSettings = p2pSettingsService.findP2pSettingsByMerchantId(cashbox.getMerchantId());
        if (Objects.isNull(merchantP2pSettings)) {
            return 0L;
        }
        return merchantP2pSettings.getDefaultCardId();
    }

    public String getUrlByPayment(Payment payment) {
        try {
            if (payment.getStatus().equals(SUCCESS)) {
                return cashboxSettingsService.getField(payment.getCashboxId(), REDIRECT_SUCCESS_URL);
            } else if (payment.getStatus().equals(FAILED)) {
                return cashboxSettingsService.getField(payment.getCashboxId(), REDIRECT_FAILED_URL);
            } else {
                return cashboxSettingsService.getField(payment.getCashboxId(), REDIRECT_PENDING_URL);
            }
        } catch (Exception e) {
            logger.error(payment.getCashboxId().toString());
            e.printStackTrace();
        }
        return null;
    }

    public String getSecret(Long cashboxid) {
        String secret = cashboxSettingsService.getField(cashboxid, SECRET);
        if (secret.length() == 0) {
            StringBuilder sb = new StringBuilder();
            String dictionary = "0123456789QWERTYUPASDFGHJKLZXCVBNMqwertyuipasdfghjkzxcvnm";
            for (int i = 0; i < 16; i++) {
                char c = dictionary.charAt(random.nextInt(dictionary.length()));
                sb.append(c);
            }
            secret = sb.toString();
            cashboxSettingsService.setField(cashboxid, SECRET, secret);
        }
        return secret;
    }

    public String getRedirectForPayment(Payment payment) {
        Cashbox cashbox = cashboxRepository.findById(payment.getCashboxId()).orElse(null);
        if (cashbox == null) {
            logger.error("Cashbox: {}", cashbox);
            return "error";
        }
        String location = cashboxSettingsService.getField(payment.getCashboxId(), REDIRECT_URL)
                + "?status=" + payment.getStatus()
                + (Objects.nonNull(payment.getBillId()) ? "?billid=" + payment.getBillId() : "")
                + "&paymentid=" + payment.getGuid();
        if (payment.getParam() != null && payment.getParam().length() > 0) {
            location = location + "&param=" + URLEncoder.encode(payment.getParam(), Charset.defaultCharset());
        }
        return location;
    }

    public String getInteractUrl(Payment payment) {
        return cashboxSettingsService.getField(payment.getCashboxId(), INTERACTION_URL);
    }

    public String getInteractUrl(Long cashBoxId) {
        return cashboxSettingsService.getField(cashBoxId, INTERACTION_URL);
    }

    public ResultDTO all() {
        try {
            List<Cashbox> cashboxList = cashboxRepository.findByDeleted(false);
            return new ResultDTO(true, addBalance(cashboxList), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO setCashBoxCard(SetCashBoxCardDto dto) {
        Optional<Cashbox> optionalCashBox = cashboxRepository.findById(dto.getCashBoxId());
        if (optionalCashBox.isEmpty()) {
            return ErrorDictionary.CASHBOX_NOT_FOUND;
        }
        Cashbox cashbox = optionalCashBox.get();
        if (!cashbox.getMerchantId().equals(dto.getMerchantId())) {
            return ErrorDictionary.AVAILABLE_ONLY_FOR_CASHBOXES;
        }
        UserCard userCard = userCardRepository.findById(dto.getCardId()).orElse(null);
        if (Objects.isNull(userCard)) {
            return ErrorDictionary.CARD_NOT_FOUND;
        }
        if (!userCard.getUserId().equals(dto.getMerchantId())) {
            return ErrorDictionary.AVAILABLE_ONLY_FOR_CASHBOX_OWNER;
        }

        MerchantP2pSettings merchantP2pSettings = p2pSettingsService.findP2pSettingsByMerchantId(cashbox.getMerchantId());
        cashbox.setUserCardId(dto.getCardId());
        cashbox.setUseDefaultCard(cashbox.getUserCardId().equals(merchantP2pSettings.getDefaultCardId()));
        cashbox = cashboxRepository.save(cashbox);

        CashBoxP2pDto cashBoxP2pDto = new CashBoxP2pDto(cashbox.getId(), cashbox.getMerchantId(),
                cashbox.getUserCardId(), userCard.getCardNumber(), cashbox.isP2pAllowed(), cashbox.isUseDefaultCard());
        return new ResultDTO(true, cashBoxP2pDto, 0);
    }

    public ResultDTO setBankCashBoxCard(SetCashBoxCardDto dto) {
        logger.info("setBankCashBoxCard()");
        Optional<Cashbox> optionalCashBox = cashboxRepository.findById(dto.getCashBoxId());
        if (optionalCashBox.isEmpty()) {
            return ErrorDictionary.CASHBOX_NOT_FOUND;
        }
        Cashbox cashbox = optionalCashBox.get();
        if (!cashbox.getMerchantId().equals(dto.getMerchantId())) {
            return ErrorDictionary.AVAILABLE_ONLY_FOR_CASHBOXES;
        }
        UserCardFromBank userCard = userBankCardRepository.findById(dto.getCardId()).orElse(null);
        if (Objects.isNull(userCard)) {
            return ErrorDictionary.CARD_NOT_FOUND;
        }
        if (!userCard.getUserId().equals(dto.getMerchantId())) {
            return ErrorDictionary.AVAILABLE_ONLY_FOR_CASHBOX_OWNER;
        }

        MerchantP2pSettings merchantP2pSettings = p2pSettingsService.findP2pSettingsByMerchantId(cashbox.getMerchantId());
        cashbox.setUserCardId(dto.getCardId());
        cashbox.setUseDefaultCard(cashbox.getUserCardId().equals(merchantP2pSettings.getDefaultCardId()));
        cashbox = cashboxRepository.save(cashbox);

        CashBoxP2pDto cashBoxP2pDto = new CashBoxP2pDto(cashbox.getId(), cashbox.getMerchantId(),
                cashbox.getUserCardId(), userCard.getCardNumber(), cashbox.isP2pAllowed(), cashbox.isUseDefaultCard());
        return new ResultDTO(true, cashBoxP2pDto, 0);
    }

    public ResultDTO getCashBoxP2pSettings(Long cashBoxId) {
        Optional<Cashbox> optionalCashBox = cashboxRepository.findById(cashBoxId);
        if (optionalCashBox.isEmpty()) {
            return ErrorDictionary.CASHBOX_NOT_FOUND;
        }

        Cashbox cashbox = optionalCashBox.get();
        CashBoxP2pDto cashBoxP2pDto = new CashBoxP2pDto();
        cashBoxP2pDto.setId(cashbox.getId());
        cashBoxP2pDto.setMerchantId(cashbox.getMerchantId());
        cashBoxP2pDto.setP2pAllowed(cashbox.isP2pAllowed());
        cashBoxP2pDto.setUseDefaultCard(cashbox.isUseDefaultCard());
        if (Objects.isNull(cashbox.getUserCardId())) {
            cashBoxP2pDto.setCardId(null);
            cashBoxP2pDto.setCardNumber(null);
            return new ResultDTO(true, cashBoxP2pDto, 0);
        }

        UserCard userCard = userCardRepository.findById(cashbox.getUserCardId()).orElse(null);
        if (Objects.isNull(userCard)) {
            cashBoxP2pDto.setCardId(null);
            cashBoxP2pDto.setCardNumber(null);
            return new ResultDTO(true, cashBoxP2pDto, 0);
        }

        cashBoxP2pDto.setCardId(userCard.getId());
        cashBoxP2pDto.setCardNumber(userCard.getCardNumber());
        return new ResultDTO(true, cashBoxP2pDto, 0);
    }

    public ResultDTO getBankCashBoxP2pSettings(Long cashBoxId) {
        Optional<Cashbox> optionalCashBox = cashboxRepository.findById(cashBoxId);
        if (optionalCashBox.isEmpty()) {
            return ErrorDictionary.CASHBOX_NOT_FOUND;
        }

        Cashbox cashbox = optionalCashBox.get();
        CashBoxP2pDto cashBoxP2pDto = new CashBoxP2pDto();
        cashBoxP2pDto.setId(cashbox.getId());
        cashBoxP2pDto.setMerchantId(cashbox.getMerchantId());
        cashBoxP2pDto.setP2pAllowed(cashbox.isP2pAllowed());
        cashBoxP2pDto.setUseDefaultCard(cashbox.isUseDefaultCard());
        if (Objects.isNull(cashbox.getUserCardId())) {
            cashBoxP2pDto.setCardId(null);
            cashBoxP2pDto.setCardNumber(null);
            return new ResultDTO(true, cashBoxP2pDto, 0);
        }

        UserCardFromBank userCard = userBankCardRepository.findById(cashbox.getUserCardId()).orElse(null);
        if (Objects.isNull(userCard)) {
            cashBoxP2pDto.setCardId(null);
            cashBoxP2pDto.setCardNumber(null);
            return new ResultDTO(true, cashBoxP2pDto, 0);
        }

        cashBoxP2pDto.setCardId(userCard.getId());
        cashBoxP2pDto.setCardNumber(userCard.getCardNumber());
        return new ResultDTO(true, cashBoxP2pDto, 0);
    }

    public ResultDTO setCashBoxP2pSettings(SetCashBoxP2pSettingsDto dto) {
        Optional<Cashbox> optionalCashBox = cashboxRepository.findById(dto.getCashBoxId());
        if (optionalCashBox.isEmpty()) {
            return ErrorDictionary.CASHBOX_NOT_FOUND;
        }
        Cashbox cashbox = optionalCashBox.get();
        cashbox.setP2pAllowed(dto.isP2pAllowed());
        cashbox.setUseDefaultCard(dto.isUseDefaultCard());

        UserCard userCard = null;
        if (dto.isUseDefaultCard()) {
            MerchantP2pSettings merchantP2pSettings = p2pSettingsService.findP2pSettingsByMerchantId(cashbox.getMerchantId());
            userCard = userCardRepository.findById(merchantP2pSettings.getDefaultCardId()).orElse(null);
        } else {
            userCard = userCardRepository.findById(cashbox.getUserCardId()).orElse(null);
        }

        CashBoxP2pDto cashBoxP2pDto = new CashBoxP2pDto();
        cashBoxP2pDto.setId(cashbox.getId());
        cashBoxP2pDto.setMerchantId(cashbox.getMerchantId());
        cashBoxP2pDto.setP2pAllowed(cashbox.isP2pAllowed());
        cashBoxP2pDto.setUseDefaultCard(cashbox.isUseDefaultCard());
        if (Objects.isNull(userCard)) {
            cashBoxP2pDto.setCardId(null);
            cashBoxP2pDto.setCardNumber(null);
            return new ResultDTO(true, cashBoxP2pDto, 0);
        }

        cashbox.setUserCardId(userCard.getId());
        cashboxRepository.save(cashbox);

        cashBoxP2pDto.setCardId(userCard.getId());
        cashBoxP2pDto.setCardNumber(userCard.getCardNumber());
        return new ResultDTO(true, cashBoxP2pDto, 0);
    }

    public ResultDTO setBankCashBoxP2pSettings(SetCashBoxP2pSettingsDto dto) {
        Optional<Cashbox> optionalCashBox = cashboxRepository.findById(dto.getCashBoxId());
        if (optionalCashBox.isEmpty()) {
            return ErrorDictionary.CASHBOX_NOT_FOUND;
        }
        Cashbox cashbox = optionalCashBox.get();
        cashbox.setP2pAllowed(dto.isP2pAllowed());
        cashbox.setUseDefaultCard(dto.isUseDefaultCard());

        UserCardFromBank userCard = null;
        if (dto.isUseDefaultCard()) {
            MerchantP2pSettings merchantP2pSettings = p2pSettingsService.findP2pSettingsByMerchantId(cashbox.getMerchantId());
            userCard = userBankCardRepository.findById(merchantP2pSettings.getDefaultCardId()).orElse(null);
        } else {
            userCard = userBankCardRepository.findById(cashbox.getUserCardId()).orElse(null);
        }

        CashBoxP2pDto cashBoxP2pDto = new CashBoxP2pDto();
        cashBoxP2pDto.setId(cashbox.getId());
        cashBoxP2pDto.setMerchantId(cashbox.getMerchantId());
        cashBoxP2pDto.setP2pAllowed(cashbox.isP2pAllowed());
        cashBoxP2pDto.setUseDefaultCard(cashbox.isUseDefaultCard());
        if (Objects.isNull(userCard)) {
            cashBoxP2pDto.setCardId(null);
            cashBoxP2pDto.setCardNumber(null);
            return new ResultDTO(true, cashBoxP2pDto, 0);
        }

        cashbox.setUserCardId(userCard.getId());
        cashboxRepository.save(cashbox);

        cashBoxP2pDto.setCardId(userCard.getId());
        cashBoxP2pDto.setCardNumber(userCard.getCardNumber());
        return new ResultDTO(true, cashBoxP2pDto, 0);
    }

    private List<CashboxDTO> addBalance(List<Cashbox> cashboxList) {
        List<CashboxDTO> result = new ArrayList<>();
        for (Cashbox cashbox : cashboxList) {
            result.add(addBalance(cashbox));
        }
        return result;
    }

    private CashboxDTO addBalance(Cashbox cashbox) {
        CashboxDTO cashboxDTO = new CashboxDTO();
        cashboxDTO.setId(cashbox.getId());
        cashboxDTO.setName(cashbox.getName());
        cashboxDTO.setMerchantId(cashbox.getMerchantId());
        cashboxDTO.setDeleted(cashbox.isDeleted());
        cashboxDTO.setBalance(getBalance(cashbox.getId()));
        return cashboxDTO;
    }

    private List<CashboxBalanceDTO> getBalance(Long cashboxId) {
        Map<String, BigDecimal> result = new HashMap<>();
        List<Payment> paymentList = paymentRepository.findByCashboxIdAndStatus(cashboxId, SUCCESS);

        for (Payment payment : paymentList) {
            BigDecimal amount = BigDecimal.ZERO;
            if (result.containsKey(payment.getCurrency())) {
                amount = result.get(payment.getCurrency());
            }
            amount = amount.add(payment.getTotalAmount());
            result.put(payment.getCurrency(), amount);
        }
        logger.info("result " + result.toString());
        return result.entrySet().stream()
                .map(o -> new CashboxBalanceDTO(o.getKey(), o.getValue()))
                .collect(Collectors.toList());
    }

    public Cashbox getCashboxByOrderId(String orderid) {
        Payment payment = paymentRepository.findTopByPaySysPayId(orderid);
        return findById(payment.getCashboxId());
    }

}
