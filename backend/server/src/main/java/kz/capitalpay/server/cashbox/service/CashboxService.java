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
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.payments.service.PaymentService;
import kz.capitalpay.server.usercard.model.UserCard;
import kz.capitalpay.server.usercard.repository.UserCardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.*;

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
                return error112;
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
                return error113;
            }
            if (!cashbox.getMerchantId().equals(owner.getId())) {
                return error110;
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
                return error113;
            }
            if (!cashbox.getMerchantId().equals(owner.getId())) {
                return error110;
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
        return cashboxRepository.findCardById(cashBoxId);
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
                + "?billid=" + payment.getBillId()
                + "&status=" + payment.getStatus()
                + "&paymentid=" + payment.getGuid();
        if (payment.getParam() != null && payment.getParam().length() > 0) {
            location = location + "&param=" + URLEncoder.encode(payment.getParam(), Charset.defaultCharset());
        }

        return location;
    }

    public String getInteractUrl(Payment payment) {
        String interactionUrl = cashboxSettingsService.getField(payment.getCashboxId(), INTERACTION_URL);
        return interactionUrl;
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
            return ErrorDictionary.error113;
        }
        Cashbox cashbox = optionalCashBox.get();
        if (!cashbox.getMerchantId().equals(dto.getMerchantId())) {
            return ErrorDictionary.error122;
        }

        UserCard userCard = userCardRepository.findById(dto.getCardId()).orElse(null);
        if (Objects.isNull(userCard)) {
            return ErrorDictionary.error130;
        }
        if (!userCard.getUserId().equals(dto.getMerchantId())) {
            return ErrorDictionary.error133;
        }

        cashbox.setUserCardId(dto.getCardId());
        cashbox = cashboxRepository.save(cashbox);

        CashBoxP2pDto cashBoxP2pDto = new CashBoxP2pDto(cashbox.getId(), cashbox.getMerchantId(),
                cashbox.getUserCardId(), userCard.getCardNumber(), cashbox.isP2pAllowed());
        return new ResultDTO(true, cashBoxP2pDto, 0);
    }

    public ResultDTO getCashBoxP2pSettings(Long cashBoxId) {
        Optional<Cashbox> optionalCashBox = cashboxRepository.findById(cashBoxId);
        if (optionalCashBox.isEmpty()) {
            return ErrorDictionary.error113;
        }
        Cashbox cashbox = optionalCashBox.get();
        UserCard userCard = userCardRepository.findById(cashbox.getUserCardId()).orElse(null);
        CashBoxP2pDto cashBoxP2pDto = new CashBoxP2pDto();
        cashBoxP2pDto.setId(cashbox.getId());
        cashBoxP2pDto.setMerchantId(cashbox.getMerchantId());
        cashBoxP2pDto.setP2pAllowed(cashbox.isP2pAllowed());
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
            return ErrorDictionary.error113;
        }
        Cashbox cashbox = optionalCashBox.get();
        cashbox.setP2pAllowed(dto.isP2pAllowed());
        cashbox = cashboxRepository.save(cashbox);
        UserCard userCard = userCardRepository.findById(cashbox.getUserCardId()).orElse(null);
        CashBoxP2pDto cashBoxP2pDto = new CashBoxP2pDto();
        if (Objects.isNull(userCard)) {
            cashBoxP2pDto.setCardId(null);
            cashBoxP2pDto.setCardNumber(null);
            cashBoxP2pDto.setId(cashbox.getId());
            cashBoxP2pDto.setMerchantId(cashbox.getMerchantId());
            cashBoxP2pDto.setP2pAllowed(cashbox.isP2pAllowed());
            return new ResultDTO(true, cashBoxP2pDto, 0);
        }

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

        cashboxDTO.setBalance(paymentService.getBalance(cashbox.getId()));
        return cashboxDTO;
    }


}
