package kz.capitalpay.server.paysystems.systems.halyksoap.service;

import kz.capitalpay.server.merchantsettings.model.CashboxSettings;
import kz.capitalpay.server.merchantsettings.model.MerchantKyc;
import kz.capitalpay.server.merchantsettings.repository.CashboxSettingsRepository;
import kz.capitalpay.server.merchantsettings.repository.MerchantKycRepository;
import kz.capitalpay.server.paysystems.systems.halyksoap.dto.HalykDTO;
import kz.capitalpay.server.paysystems.systems.halyksoap.dto.PaymentStatistic;
import kz.capitalpay.server.paysystems.systems.halyksoap.dto.RegisterPaymentHalykDTO;
import kz.capitalpay.server.paysystems.systems.halyksoap.dto.RegisterPaymentMerchantDTO;
import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykSettings;
import kz.capitalpay.server.paysystems.systems.halyksoap.repository.HalykSettingsRepository;
import kz.capitalpay.server.paysystems.systems.halyksoap.repository.PaymentStatisticRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static kz.capitalpay.server.merchantsettings.service.CashboxSettingsService.PERCENT_PAYMENT_SYSTEM;
import static kz.capitalpay.server.merchantsettings.service.MerchantKycService.*;
import static kz.capitalpay.server.paysystems.systems.halyksoap.service.HalykSettingsService.*;

@Service
public class RegisterPaymentsService {
    private final static String CONSTANT_NAME_REGISTER = "register";

    private final Logger logger = LoggerFactory.getLogger(RegisterPaymentsService.class);

    @Autowired
    private PaymentStatisticRepository paymentStatisticRepository;

    @Autowired
    private MerchantKycRepository merchantKycRepository;

    @Autowired
    private HalykSettingsRepository halykSettingsRepository;

    @Autowired
    private CashboxSettingsRepository cashboxSettingsRepository;

    public File createTextFileForDownload(HalykDTO halykDTO) {
        String nameFile = createNameFile();
        File file = new File(nameFile);
        try (Writer writer = new BufferedWriter(new FileWriter(file))) {
            String contents = getRegisterPayments(halykDTO);
            writer.write(contents);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private String getRegisterPayments(HalykDTO halykDTO) {
        List<PaymentStatistic> statistic = getPaymentsByDate(halykDTO);
        List<RegisterPaymentMerchantDTO> register = new ArrayList<>();
        RegisterPaymentHalykDTO halyk = new RegisterPaymentHalykDTO();
        for (PaymentStatistic data : statistic) {
            RegisterPaymentMerchantDTO merchant = new RegisterPaymentMerchantDTO();
            setIndividualDataForMerchant(merchant, Long.parseLong(data.getMerchantId()));
            setHalykCommonDataForMerchant(merchant);
            logger.info(" data.getTotalAmount().toString() " + data.getTotalAmount().toString());
            divideMoneyBetweenMerchantAndHalyk(data.getCashboxId(), Double.parseDouble(data.getTotalAmount().toString()), halyk, merchant);
            register.add(merchant);
        }
        setIndividualInfoForHalyk(halyk);
        return register.toString().replace("[", "").replace("]", "") + halyk.toString();
    }

    private void setIndividualInfoForHalyk(RegisterPaymentHalykDTO halyk) {
        HalykSettings kobd = halykSettingsRepository.findTopByFieldName(KOBD);
        HalykSettings lskor = halykSettingsRepository.findTopByFieldName(LSKOR);
        HalykSettings rnnb = halykSettingsRepository.findTopByFieldName(RNNB);
        HalykSettings poluch = halykSettingsRepository.findTopByFieldName(POLUCH);
        HalykSettings naznpl = halykSettingsRepository.findTopByFieldName(NAZNPL);
        HalykSettings bclassd = halykSettingsRepository.findTopByFieldName(BCLASSD);
        HalykSettings kod = halykSettingsRepository.findTopByFieldName(KOD);
        HalykSettings knp = halykSettingsRepository.findTopByFieldName(KNP);
        HalykSettings rnna = halykSettingsRepository.findTopByFieldName(RNNA);
        HalykSettings platel = halykSettingsRepository.findTopByFieldName(PLATEL);
        halyk.setKobd(kobd.getFieldValue());
        halyk.setLskor(lskor.getFieldValue());
        halyk.setRnnb(rnnb.getFieldValue());
        halyk.setPoluch(poluch.getFieldValue());
        halyk.setNaznpl(naznpl.getFieldValue());
        halyk.setBclassd(bclassd.getFieldValue());
        halyk.setKod(kod.getFieldValue());
        halyk.setKnp(knp.getFieldValue());
        halyk.setRnna(rnna.getFieldValue());
        halyk.setPlatel(platel.getFieldValue());
    }

    private void divideMoneyBetweenMerchantAndHalyk(long cashBoxId, Double amount, RegisterPaymentHalykDTO halyk,
                                                    RegisterPaymentMerchantDTO merchant) {
        CashboxSettings percentForHalyk = cashboxSettingsRepository
                .findTopByFieldNameAndCashboxId(PERCENT_PAYMENT_SYSTEM, cashBoxId);
        double percent = 0.0;
        try {
            percent = Double.parseDouble(percentForHalyk.getFieldValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        double halykMoney = amount * percent / 100;
        double merchantMoney = amount - halykMoney;
        double currentHalykMoney = halyk.getAmount() == null ? 0.0 : Double.parseDouble(halyk.getAmount());
        halyk.setAmount(String.valueOf(halykMoney + currentHalykMoney));
        merchant.setAmount(String.valueOf(merchantMoney));
    }

    private void setHalykCommonDataForMerchant(RegisterPaymentMerchantDTO merchant) {
        HalykSettings naznpl_merch = halykSettingsRepository.findTopByFieldName(NAZNPL_MERCH);
        HalykSettings bclassd_merch = halykSettingsRepository.findTopByFieldName(BCLASSD_MERCH);
        HalykSettings kod_merch = halykSettingsRepository.findTopByFieldName(KOD_MERCH);
        HalykSettings knp_merch = halykSettingsRepository.findTopByFieldName(KNP_MERCH);
        HalykSettings rnna_merch = halykSettingsRepository.findTopByFieldName(RNNA_MERCH);
        HalykSettings platel_merch = halykSettingsRepository.findTopByFieldName(PLATEL_MERCH);
        merchant.setNaznpl_merch(naznpl_merch.getFieldValue());
        merchant.setBclassd_merch(bclassd_merch.getFieldValue());
        merchant.setKod_merch(kod_merch.getFieldValue());
        merchant.setKnp_merch(knp_merch.getFieldValue());
        merchant.setRnna_merch(rnna_merch.getFieldValue());
        merchant.setPlatel_merch(platel_merch.getFieldValue());
    }

    private void setIndividualDataForMerchant(RegisterPaymentMerchantDTO merchant,
                                              Long merchantId) {
        MerchantKyc iikInfo = merchantKycRepository.findTopByFieldNameAndMerchantId(IIK, merchantId);
        MerchantKyc bikInfo = merchantKycRepository.findTopByFieldNameAndMerchantId(BIK, merchantId);
        MerchantKyc iinbinInfo = merchantKycRepository.findTopByFieldNameAndMerchantId(IINBIN, merchantId);
        MerchantKyc banknameInfo = merchantKycRepository.findTopByFieldNameAndMerchantId(BANKNAME, merchantId);
        merchant.setIik(iikInfo.getFieldValue());
        merchant.setBik(bikInfo.getFieldValue());
        merchant.setIinbin(iinbinInfo.getFieldValue());
        merchant.setBankname(banknameInfo.getFieldValue());
    }

    private List<PaymentStatistic> getPaymentsByDate(HalykDTO halykDTO) {
        LocalDateTime tomorrow = halykDTO.getDateFrom().minusDays(1L);
        LocalDateTime yesterday = halykDTO.getDateTo().plusDays(1L);
        return paymentStatisticRepository.findAllByLocalDateTimeIsBeforeAndLocalDateTimeIsAfterAndStatus(tomorrow, yesterday, "SUCCESS");
    }

    private String createNameFile() {
        StringBuilder nameFile = new StringBuilder();
        nameFile.append(CONSTANT_NAME_REGISTER);
        HalykSettings orderNumber = halykSettingsRepository.findTopByFieldName(ORDER_NUMBER_REPORT);
        int number = orderNumber == null ? 0 : Integer.parseInt(orderNumber.getFieldValue());
        LocalDateTime dateNow = LocalDateTime.now();

        if (number == 0) {
            number = 1;
            nameFile.append(number)
                    .append(formatViewDayOrMonth(dateNow.getMonthValue()))
                    .append(formatViewDayOrMonth(dateNow.getDayOfMonth()))
                    .append(".txt");
            saveNumberAndDateDownloadRegister(dateNow, number);
            return nameFile.toString();
        }
        HalykSettings lastDownloadsRegister = halykSettingsRepository.findTopByFieldName(DATE_LAST_DOWNLOADS);
        LocalDateTime lastDownloads = LocalDateTime.parse(lastDownloadsRegister.getFieldValue());
        if (dateNow.getYear() == lastDownloads.getYear() && dateNow.getMonth() == lastDownloads.getMonth()
                && dateNow.getDayOfMonth() == lastDownloads.getDayOfMonth()) {
            logger.info("before " + number);
            number = number + 1;
            logger.info("after " + number);
            nameFile.append(number);
        } else {
            number = 1;
            nameFile.append(number);
        }
        nameFile.append(formatViewDayOrMonth(dateNow.getMonthValue()))
                .append(formatViewDayOrMonth(dateNow.getDayOfMonth()))
                .append(".txt");
        logger.info("before save " + number);
        saveNumberAndDateDownloadRegister(orderNumber.getId(),dateNow, number);
        return nameFile.toString();
    }

    private String formatViewDayOrMonth(int dayOrMonth) {
        return dayOrMonth < 10 ? "0" + dayOrMonth : String.valueOf(dayOrMonth);
    }

    private void saveNumberAndDateDownloadRegister(Long halykId, LocalDateTime localDateTime, int numberOrder) {
        HalykSettings halykSettingsDate = new HalykSettings();
        halykSettingsDate.setId(halykId);
        halykSettingsDate.setFieldName(DATE_LAST_DOWNLOADS);
        halykSettingsDate.setFieldValue(localDateTime.toString());
        halykSettingsRepository.save(halykSettingsDate);
        HalykSettings halykSettingsNumber = new HalykSettings();
        halykSettingsNumber.setId(halykId);
        halykSettingsNumber.setFieldName(ORDER_NUMBER_REPORT);
        halykSettingsNumber.setFieldValue(String.valueOf(numberOrder));
        halykSettingsRepository.save(halykSettingsNumber);
    }

    private void saveNumberAndDateDownloadRegister(LocalDateTime localDateTime, int numberOrder) {
        HalykSettings halykSettingsDate = new HalykSettings();
        halykSettingsDate.setFieldName(DATE_LAST_DOWNLOADS);
        halykSettingsDate.setFieldValue(localDateTime.toString());
        halykSettingsRepository.save(halykSettingsDate);
        HalykSettings halykSettingsNumber = new HalykSettings();
        halykSettingsNumber.setFieldName(ORDER_NUMBER_REPORT);
        halykSettingsNumber.setFieldValue(String.valueOf(numberOrder));
        halykSettingsRepository.save(halykSettingsNumber);
    }
}
