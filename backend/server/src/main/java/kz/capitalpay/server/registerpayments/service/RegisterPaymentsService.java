package kz.capitalpay.server.registerpayments.service;

import kz.capitalpay.server.merchantsettings.model.MerchantKyc;
import kz.capitalpay.server.merchantsettings.repository.MerchantKycRepository;
import kz.capitalpay.server.payments.repository.PaymentRepository;
import kz.capitalpay.server.registerpayments.dto.PaymentStatistic;
import kz.capitalpay.server.registerpayments.dto.RegisterPaymentDTO;
import kz.capitalpay.server.registerpayments.repository.PaymentStatisticRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RegisterPaymentsService {
    private final Logger logger = LoggerFactory.getLogger(RegisterPaymentsService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentStatisticRepository paymentStatisticRepository;

    @Autowired
    private MerchantKycRepository merchantKycRepository;

    public File createTextFileForDownload(LocalDateTime localDate) {
        File file = new File("write.txt");
        try (Writer writer = new BufferedWriter(new FileWriter(file))) {
            String contents = getRegisterPayments(localDate);
            writer.write(contents);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private String getRegisterPayments(LocalDateTime localDate) {
        List<RegisterPaymentDTO> register = new ArrayList<>();
        List<PaymentStatistic> payment = getPaymentsByDate(localDate);
        logger.info("paymentStatistic " + payment.toString());
        for (PaymentStatistic data: payment) {
            RegisterPaymentDTO registerPayments = new RegisterPaymentDTO();
            registerPayments.setTotalAmountPayment(String.valueOf(data.getTotalAmount()));
            MerchantKyc merchantKyc = merchantKycRepository.findTopByFieldNameAndMerchantId("bankname", 29L);
            registerPayments.setReceivingMerchantBank(merchantKyc.getFieldValue());
            register.add(registerPayments);
        }
        logger.info("registerPayments " + register.toString());
        return register.toString();
    }

    private List<PaymentStatistic> getPaymentsByDate(LocalDateTime localDate) {
        LocalDateTime tomorrow = localDate.minusDays(1L);
        LocalDateTime yesterday = localDate.plusDays(1L);
        return  paymentStatisticRepository.findTopByLocalDateTime(tomorrow, yesterday);
    }
}
