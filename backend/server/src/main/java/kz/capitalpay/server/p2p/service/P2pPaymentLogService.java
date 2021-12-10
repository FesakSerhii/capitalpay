package kz.capitalpay.server.p2p.service;

import kz.capitalpay.server.p2p.model.P2pPaymentLog;
import kz.capitalpay.server.p2p.repository.P2pPaymentLogRepository;
import kz.capitalpay.server.payments.service.PaymentLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class P2pPaymentLogService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentLogService.class);
    private final P2pPaymentLogRepository p2pPaymentLogRepository;

    public P2pPaymentLogService(P2pPaymentLogRepository p2pPaymentLogRepository) {
        this.p2pPaymentLogRepository = p2pPaymentLogRepository;
    }

    public void newEvent(String paymentID, String who, String action, String details) {
        P2pPaymentLog paymentLog = new P2pPaymentLog();
        paymentLog.setTimestamp(System.currentTimeMillis());
        paymentLog.setPaymentID(paymentID);
        paymentLog.setWho(who);
        paymentLog.setAction(action);
        paymentLog.setDetails(details);
        p2pPaymentLogRepository.save(paymentLog);
    }
}
