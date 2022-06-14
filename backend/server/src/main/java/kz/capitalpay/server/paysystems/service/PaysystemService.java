package kz.capitalpay.server.paysystems.service;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.cashbox.service.CashboxPaysystemService;
import kz.capitalpay.server.cashbox.service.CashboxService;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.eventlog.service.SystemEventsLogsService;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.merchantsettings.service.CashboxSettingsService;
import kz.capitalpay.server.merchantsettings.service.MerchantKycService;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.payments.service.PaymentService;
import kz.capitalpay.server.paysystems.dto.ActivatePaysystemDTO;
import kz.capitalpay.server.paysystems.dto.BillPaymentDto;
import kz.capitalpay.server.paysystems.dto.PaySystemButonResponceDTO;
import kz.capitalpay.server.paysystems.dto.PaymentRequestDTO;
import kz.capitalpay.server.paysystems.model.PaysystemInfo;
import kz.capitalpay.server.paysystems.repository.PaysystemInfoRepository;
import kz.capitalpay.server.paysystems.systems.PaySystem;
import kz.capitalpay.server.paysystems.systems.halyksoap.service.HalykSoapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.*;

import static kz.capitalpay.server.constants.ErrorDictionary.PAYMENT_NOT_FOUND;
import static kz.capitalpay.server.constants.ErrorDictionary.PAYSYSTEM_NOT_FOUND;
import static kz.capitalpay.server.eventlog.service.SystemEventsLogsService.ACTIVATE_PAYSYSTEM;
import static kz.capitalpay.server.merchantsettings.service.CashboxSettingsService.CLIENT_FEE;
import static kz.capitalpay.server.merchantsettings.service.MerchantKycService.TOTAL_FEE;

@Service
public class PaysystemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaysystemService.class);

    Gson gson;
    PaysystemInfoRepository paysystemInfoRepository;
    ApplicationUserService applicationUserService;
    SystemEventsLogsService systemEventsLogsService;
    PaymentService paymentService;
    CashboxPaysystemService cashboxPaysystemService;
    MerchantKycService merchantKycService;
    CashboxSettingsService cashboxSettingsService;
    List<PaySystem> paySystemList;
    CashboxService cashboxService;


    @Value("${remote.api.addres}")
    String apiAddress;

    @Autowired
    HalykSoapService halykSoapService;

    Map<String, PaySystem> paySystems = new HashMap<>();

    public ResultDTO systemList() {
        try {
            List<PaysystemInfo> systemList = paysystemList();
            return new ResultDTO(true, systemList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public List<PaysystemInfo> paysystemList() {
        List<PaysystemInfo> paysystemInfoList = paysystemInfoRepository.findAll();
        if (paysystemInfoList.size() == 0) {
            paysystemInfoList = new ArrayList<>();
            PaysystemInfo paysystemInfo = new PaysystemInfo();
            paysystemInfo.setName("Test PaySystem");
            paysystemInfo.setEnabled(true);
            paysystemInfoRepository.save(paysystemInfo);
            paysystemInfoList.add(paysystemInfo);
        }
        LOGGER.info("paysystemInfoList: {}", gson.toJson(paysystemInfoList));
        return paysystemInfoList;
    }


    public ResultDTO enablePaysystem(Principal principal, ActivatePaysystemDTO request) {
        try {
            ApplicationUser operator = applicationUserService.getUserByLogin(principal.getName());
            PaysystemInfo paysystemInfo = paysystemInfoRepository.findById(request.getPaysystemId()).orElse(null);
            if (paysystemInfo == null) {
                return PAYSYSTEM_NOT_FOUND;
            }
            paysystemInfo.setEnabled(request.getEnabled());
            systemEventsLogsService.addNewOperatorAction(operator.getUsername(), ACTIVATE_PAYSYSTEM,
                    gson.toJson(request), "all");
            paysystemInfoRepository.save(paysystemInfo);
            return new ResultDTO(true, paysystemInfo, 0);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    @PostConstruct
    public void createPaySystemsMap() {
        if (paySystemList != null && paySystemList.size() > 0) {
            for (PaySystem paySystem : paySystemList) {
                String componentName = paySystem.getComponentName();
                LOGGER.info(componentName);
                paySystems.put(componentName, paySystem);
            }
        } else {
            LOGGER.error("PaySystem List not found");
        }
    }

    public ResultDTO systemButtonList(PaymentRequestDTO request) {
        try {
            List<PaySystemButonResponceDTO> result = new ArrayList<>();
            Payment payment = paymentService.getPayment(request.getPaymentId());
            if (payment == null) {
                return PAYMENT_NOT_FOUND;
            }
            List<PaysystemInfo> availablePaysystems = cashboxPaysystemService
                    .availablePaysystemList(payment.getCashboxId());
            availablePaysystems.sort(Comparator.comparing(PaysystemInfo::getPriority));
            LOGGER.info(gson.toJson(availablePaysystems));
            for (PaysystemInfo pi : availablePaysystems) {
                PaySystemButonResponceDTO paySystemButon = new PaySystemButonResponceDTO();
                paySystemButon.setPaysystemInfo(pi);
                PaySystem paySystem = paySystems.get(pi.getComponentName());
                paySystemButon.setPaymentForm(paySystem.getPaymentButton(payment));
                result.add(paySystemButon);
            }
            return new ResultDTO(true, result, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private BillPaymentDto createBill(Payment payment, HttpServletRequest httpRequest, String cardHolderName, String pan, String result) {
        BillPaymentDto billPaymentDto = new BillPaymentDto();
        billPaymentDto.setResultPayment(result);
        setAmountFields(payment.getCashboxId(), payment.getTotalAmount(), billPaymentDto, payment.getCurrency());
        billPaymentDto.setMerchantName(payment.getMerchantName());
        billPaymentDto.setNumberTransaction(payment.getPaySysPayId());
        billPaymentDto.setOrderId(payment.getBillId());
        if (!"ok".equalsIgnoreCase(result)) {
            return billPaymentDto;
        }
        billPaymentDto.setWebSiteMerchant(httpRequest.getServerName());
        billPaymentDto.setDateTransaction(payment.getLocalDateTime());
        billPaymentDto.setTypeTransaction(1);
        billPaymentDto.setCardHolderName(cardHolderName);
        billPaymentDto.setCardNumber(pan);
        billPaymentDto.setPaySystemName(pan);
        billPaymentDto.setPurposePayment(payment.getDescription());
        return billPaymentDto;
    }

    private void setAmountFields(Long cashboxId, BigDecimal totalAmount, BillPaymentDto billPaymentDto, String currency) {
        BigDecimal oneHundred = new BigDecimal(100);
        Cashbox cashbox = cashboxService.findById(cashboxId);
        BigDecimal totalFee = BigDecimal.valueOf(Long.parseLong(merchantKycService.getField(cashbox.getMerchantId(),
                TOTAL_FEE)));
        BigDecimal clientFee = BigDecimal.valueOf(Long.parseLong(cashboxSettingsService
                .getField(cashboxId, CLIENT_FEE)));

        BigDecimal amountWithoutClientFee = totalAmount
                .divide((BigDecimal.ONE
                                .subtract(clientFee
                                        .divide(oneHundred, MathContext.DECIMAL128))
                                .divide(BigDecimal.ONE
                                                .subtract(totalFee
                                                        .divide(oneHundred, MathContext.DECIMAL128)),
                                        MathContext.DECIMAL128)),
                        MathContext.DECIMAL128)
                .setScale(0, RoundingMode.HALF_UP);
        billPaymentDto.setTotalAmount(totalAmount.toString(), currency);
        billPaymentDto.setAmountPayment(amountWithoutClientFee.toString(), currency);
        billPaymentDto.setAmountFee(totalAmount.subtract(amountWithoutClientFee).toString(), currency);
    }

    private HttpServletResponse redirectAfterPay(HttpServletResponse httpResponse, BillPaymentDto bill) {
        if (("OK").equals(bill.getResultPayment()) || "FAIL".equals(bill.getResultPayment())) {
            LOGGER.info("Redirect to " + bill.getResultPayment());

            String url = apiAddress + "/public/paysystem/bill" +
                    "?bill=" + gson.toJson(bill);
            httpResponse.setHeader("Location", url);

//            String location = cashboxService.getRedirectForPayment(payment);
//            httpResponse.setHeader("Location", location);
//            httpResponse.setStatus(302);
//        } else if ("FAIL".equals(bill.getResultPayment())) {
//            logger.info("Redirect to Fail");
//            httpResponse.setHeader("Location", "https://api.capitalpay.kz/public/paysystem/error");
        } else {
            LOGGER.info("Redirect to 3DS");
            LOGGER.info("Result: {}", bill.getResultPayment());
            try {
                LinkedHashMap<String, String> param = gson.fromJson(bill.getResultPayment(), LinkedHashMap.class);

                String url = apiAddress + "/public/paysystem/secure/redirect" +
                        "?acsUrl=" + param.get("acsUrl") +
                        "&MD=" + param.get("MD") +
                        "&PaReq=" + param.get("PaReq") +
                        "&bill=" + gson.toJson(bill);
                httpResponse.setHeader("Location", url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        httpResponse.setStatus(302);
        return httpResponse;
    }

    public HttpServletResponse paymentPayAndRedirect(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
                                                     String paymentid, String cardHolderName, String cvv,
                                                     String month, String pan, String year,
                                                     String phone, String email) {
        String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = httpRequest.getRemoteAddr();
        }
        LOGGER.info("Payment ID: {}", paymentid);
        LOGGER.info("Request IP: {}", ipAddress);
        LOGGER.info("Request User-Agent: {}", httpRequest.getHeader("User-Agent"));

        Payment payment = paymentService.addPhoneAndEmail(paymentid, phone, email);
        String result = halykSoapService.getPaymentOrderResult(payment.getTotalAmount(),
                cardHolderName, cvv, payment.getDescription(), month, payment.getPaySysPayId(), pan, year);
        BillPaymentDto bill = createBill(payment, httpRequest, cardHolderName, pan, result);
        return redirectAfterPay(httpResponse, bill);
    }
}
