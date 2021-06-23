package kz.capitalpay.server.paysystems.service;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.cashbox.service.CashboxPaysystemService;
import kz.capitalpay.server.cashbox.service.CashboxService;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.eventlog.service.SystemEventsLogsService;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.payments.service.PaymentService;
import kz.capitalpay.server.paysystems.dto.ActivatePaysystemDTO;
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
import java.security.Principal;
import java.util.*;

import static kz.capitalpay.server.constants.ErrorDictionary.error114;
import static kz.capitalpay.server.constants.ErrorDictionary.error118;
import static kz.capitalpay.server.eventlog.service.SystemEventsLogsService.ACTIVATE_PAYSYSTEM;

@Service
public class PaysystemService {

    Logger logger = LoggerFactory.getLogger(PaysystemService.class);

    @Autowired
    Gson gson;

    @Autowired
    PaysystemInfoRepository paysystemInfoRepository;

    @Autowired
    ApplicationUserService applicationUserService;

    @Autowired
    SystemEventsLogsService systemEventsLogsService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    CashboxPaysystemService cashboxPaysystemService;


    @Autowired
    List<PaySystem> paySystemList;

    @Autowired
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
        if (paysystemInfoList == null || paysystemInfoList.size() == 0) {
            paysystemInfoList = new ArrayList<>();
            PaysystemInfo paysystemInfo = new PaysystemInfo();
            paysystemInfo.setName("Test PaySystem");
            paysystemInfo.setEnabled(true);
            paysystemInfoRepository.save(paysystemInfo);
            paysystemInfoList.add(paysystemInfo);
        }
        logger.info("paysystemInfoList: {}",gson.toJson(paysystemInfoList));
        return paysystemInfoList;
    }


    public ResultDTO enablePaysystem(Principal principal, ActivatePaysystemDTO request) {
        try {
            ApplicationUser operator = applicationUserService.getUserByLogin(principal.getName());
            PaysystemInfo paysystemInfo = paysystemInfoRepository.findById(request.getPaysystemId()).orElse(null);
            if (paysystemInfo == null) {
                return error114;
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
                logger.info(componentName);
                paySystems.put(componentName, paySystem);
            }
        } else {
            logger.error("PaySystem List not found");
        }
    }

    public ResultDTO systemButtonList(PaymentRequestDTO request) {
        try {
            List<PaySystemButonResponceDTO> result = new ArrayList<>();

            Payment payment = paymentService.getPayment(request.getPaymentId());
            if (payment == null) {
                return error118;
            }

            List<PaysystemInfo> availablePaysystems = cashboxPaysystemService
                    .availablePaysystemList(payment.getCashboxId());
            availablePaysystems.sort(Comparator.comparing(PaysystemInfo::getPriority));
            logger.info(gson.toJson(availablePaysystems));
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

    public String transferMoney(String paymentid, String cardHolderName, String cvv,
                                String month, String pan, String year,
                                String phone, String email) {

        Payment payment = paymentService.addPhoneAndEmail(paymentid, phone, email);

        return halykSoapService.transferOrder(payment.getTotalAmount(),
                cardHolderName, cvv, payment.getDescription(), month, payment.getPaySysPayId(), pan, year);

    }

    public HttpServletResponse paymentPayAndRedirect(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
                                                     String paymentid, String cardHolderName, String cvv,
                                                     String month, String pan, String year,
                                                     String phone, String email) {

        String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = httpRequest.getRemoteAddr();
        }
        logger.info("Payment ID: {}", paymentid);
        logger.info("Request IP: {}", ipAddress);
        logger.info("Request User-Agent: {}", httpRequest.getHeader("User-Agent"));

        Payment payment = paymentService.addPhoneAndEmail(paymentid, phone, email);

        String result = halykSoapService.paymentOrder(payment.getTotalAmount(),
                cardHolderName, cvv, payment.getDescription(), month, payment.getPaySysPayId(), pan, year);
        if (result.equals("OK")) {
            logger.info("Redirect to OK");

            String location = cashboxService.getRedirectForPayment(payment);
            httpResponse.setHeader("Location", location);
            httpResponse.setStatus(302);
        } else if (result.equals("FAIL")) {
            logger.info("Redirect to Fail");
            httpResponse.setHeader("Location", "https://api.capitalpay.kz/public/paysystem/error");
            httpResponse.setStatus(302);
        } else {
            logger.info("Redirect to 3DS");
            logger.info("Result: {}", result);
            try {
                LinkedHashMap<String, String> param = gson.fromJson(result, LinkedHashMap.class);

                String url = apiAddress + "/public/paysystem/secure/redirect" +
                        "?acsUrl=" + param.get("acsUrl") +
                        "&MD=" + param.get("MD") +
                        "&PaReq=" + param.get("PaReq");

                httpResponse.setHeader("Location", url);

                httpResponse.setStatus(302);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

        return httpResponse;
    }
}
