package kz.capitalpay.server.p2p.mapper;

import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.cashbox.repository.CashboxRepository;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.repository.ApplicationUserRepository;
import kz.capitalpay.server.p2p.model.P2pPayment;
import kz.capitalpay.server.payments.model.Payment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class P2pPaymentMapper {

    private final CashboxRepository cashboxRepository;
    private final ApplicationUserRepository applicationUserRepository;

    public P2pPaymentMapper(CashboxRepository cashboxRepository, ApplicationUserRepository applicationUserRepository) {
        this.cashboxRepository = cashboxRepository;
        this.applicationUserRepository = applicationUserRepository;
    }

    public Payment toPayment(P2pPayment p2pPayment) {
        Cashbox cashbox = cashboxRepository.findById(p2pPayment.getCashboxId()).orElse(null);
        ApplicationUser merchant = applicationUserRepository.findById(p2pPayment.getMerchantId()).orElse(null);
        Payment payment = new Payment();
        payment.setCashboxId(p2pPayment.getCashboxId());
        payment.setCashboxName(Objects.nonNull(cashbox) ? cashbox.getName() : null);
        payment.setCurrency(p2pPayment.getCurrency());
        payment.setDescription("P2p payment to merchant");
        payment.setEmail(p2pPayment.getEmail());
        payment.setIpAddress(p2pPayment.getIpAddress());
        payment.setLocalDateTime(p2pPayment.getLocalDateTime());
        payment.setMerchantId(p2pPayment.getMerchantId());
        payment.setMerchantName(Objects.nonNull(merchant) ? merchant.getRealname() : null);
        payment.setParam(p2pPayment.getParam());
        payment.setPaySysPayId(p2pPayment.getOrderId());
        payment.setPhone(p2pPayment.getPhone());
        payment.setStatus(p2pPayment.getStatus());
        payment.setTimestamp(p2pPayment.getTimestamp());
        payment.setTotalAmount(p2pPayment.getTotalAmount());
        payment.setUserAgent(p2pPayment.getUserAgent());
        return payment;
    }
}
