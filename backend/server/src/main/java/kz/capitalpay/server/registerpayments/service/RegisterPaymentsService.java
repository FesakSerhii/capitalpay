package kz.capitalpay.server.registerpayments.service;

import kz.capitalpay.server.merchantsettings.model.MerchantKyc;
import kz.capitalpay.server.merchantsettings.repository.MerchantKycRepository;
import kz.capitalpay.server.payments.repository.PaymentRepository;
import kz.capitalpay.server.registerpayments.dto.ContributorDTO;
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
    private PaymentStatisticRepository paymentStatisticRepository;

    @Autowired
    private MerchantKycRepository merchantKycRepository;

    public File createTextFileForDownload(ContributorDTO contributorDTO) {
        File file = new File("write.txt");
        try (Writer writer = new BufferedWriter(new FileWriter(file))) {
            String contents = getRegisterPayments(contributorDTO);
            writer.write(contents);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private String getRegisterPayments(ContributorDTO contributorDTO) {
        List<RegisterPaymentDTO> register = new ArrayList<>();
        List<PaymentStatistic> statistic = getPaymentsByDate(contributorDTO.getPeriodRegisterPayments());
        for (PaymentStatistic data: statistic) {
            RegisterPaymentDTO registerPayments = new RegisterPaymentDTO();
            registerPayments.setTotalAmountPayment(String.valueOf(data.getTotalAmount()));
            MerchantKyc merchantKyc = merchantKycRepository.findTopByFieldNameAndMerchantId("bankname", 29L);
            registerPayments.setReceivingMerchantBank(merchantKyc.getFieldValue());
            registerPayments.setPurposePayment(contributorDTO.getCodePurposePayment());
            registerPayments.setBudgetCode(contributorDTO.getBudgetCode());
            registerPayments.setCodeSenderCash(contributorDTO.getCodeSenderCash());
            registerPayments.setCodePurposePayment(contributorDTO.getCodePurposePayment());
            registerPayments.setIinContributor(contributorDTO.getIinContributor());
            registerPayments.setContributorName(contributorDTO.getContributorName());
            register.add(registerPayments);
        }
        return register.toString();
    }

    private List<PaymentStatistic> getPaymentsByDate(LocalDateTime localDate) {
        LocalDateTime tomorrow = localDate.minusDays(1L);
        LocalDateTime yesterday = localDate.plusDays(1L);
        return  paymentStatisticRepository.findTopByLocalDateTime(tomorrow, yesterday);
    }
}
