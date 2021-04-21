package kz.capitalpay.server.payments.service;

import com.google.gson.Gson;
import kz.capitalpay.server.payments.model.PaymentLog;
import kz.capitalpay.server.payments.repository.PaymentLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentLogService {

    Logger logger = LoggerFactory.getLogger(PaymentLogService.class);

    public static final String CREATE_NEW_PAYMENT = "create new payment";

    @Autowired
    Gson gson;

    @Autowired
    PaymentLogRepository paymentLogRepository;

    public void newEvent(String paymentID, String who, String action, String details) {
        PaymentLog paymentLog = new PaymentLog();
        paymentLog.setTimestamp(System.currentTimeMillis());
        paymentLog.setPaymentID(paymentID);
        paymentLog.setWho(who);
        paymentLog.setAction(action);
        paymentLog.setDetails(details);
        paymentLogRepository.save(paymentLog);
    }

}
