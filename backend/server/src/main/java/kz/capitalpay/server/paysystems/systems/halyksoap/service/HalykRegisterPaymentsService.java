package kz.capitalpay.server.paysystems.systems.halyksoap.service;

import kz.capitalpay.server.merchantsettings.model.CashboxSettings;
import kz.capitalpay.server.merchantsettings.model.MerchantKyc;
import kz.capitalpay.server.merchantsettings.service.CashboxSettingsService;
import kz.capitalpay.server.merchantsettings.service.MerchantKycService;
import kz.capitalpay.server.paysystems.systems.halyksoap.dto.*;
import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykSettings;
import kz.capitalpay.server.paysystems.systems.halyksoap.repository.HalykSettingsRepository;
import kz.capitalpay.server.paysystems.systems.halyksoap.repository.HalykPaymentStatisticRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kz.capitalpay.server.merchantsettings.service.CashboxSettingsService.PERCENT_PAYMENT_SYSTEM;
import static kz.capitalpay.server.merchantsettings.service.MerchantKycService.*;
import static kz.capitalpay.server.paysystems.systems.halyksoap.service.HalykSettingsService.*;

@Service
public class HalykRegisterPaymentsService {
    private final static String CONSTANT_NAME_REGISTER = "register";

    private final Logger logger = LoggerFactory.getLogger(HalykRegisterPaymentsService.class);

    @Autowired
    private MerchantKycService merchantKycService;

    @Autowired
    private CashboxSettingsService cashboxSettingsService;

    @Autowired
    private HalykSettingsRepository halykSettingsRepository;

    @Autowired
    private HalykPaymentStatisticRepository halykPaymentStatisticRepository;

    public File createTextFileForDownload(RegisterPaymentsDateDTO registerPaymentsDateDTO) {
        String nameFile = createNameFile();
        File file = new File(nameFile);
        try (Writer writer = new BufferedWriter(new FileWriter(file))) {
            String contents = getRegisterPayments(registerPaymentsDateDTO);
            writer.write(contents);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private String getRegisterPayments(RegisterPaymentsDateDTO registerPaymentsDateDTO) {
        List<PaymentStatistic> statistic = getPaymentsByDate(registerPaymentsDateDTO);
        Map<String, RegisterPaymentsMerchantDTO> register = new HashMap<>();
        RegisterPaymentsCommonMerchantFieldsDTO commonMerchantFields = getHalykCommonDataForMerchant();
        RegisterPaymentsHalykDTO halyk = new RegisterPaymentsHalykDTO();
        for (PaymentStatistic data : statistic) {
            if(register.containsKey(data.getMerchantId())) {
                divideMoneyBetweenMerchantAndHalyk(data.getCashboxId(),
                        data.getTotalAmount(), halyk, register.get(data.getMerchantId()));
                continue;
            }
            RegisterPaymentsMerchantDTO merchant = new RegisterPaymentsMerchantDTO();
            setIndividualDataForMerchant(merchant, Long.parseLong(data.getMerchantId()));
            setHalykCommonDataForMerchant(merchant, commonMerchantFields);
            divideMoneyBetweenMerchantAndHalyk(data.getCashboxId(),
                    data.getTotalAmount(), halyk, merchant);
            register.put(data.getMerchantId(),merchant);
        }
        setIndividualInfoForHalyk(halyk);
        List<RegisterPaymentsMerchantDTO> result = new ArrayList<>(register.values());
        return result.toString()
                .replace("[", "")
                .replace("]", "")
                + halyk.toString();
    }

    private void setIndividualInfoForHalyk(RegisterPaymentsHalykDTO halyk) {
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

    private void divideMoneyBetweenMerchantAndHalyk(long cashBoxId, BigDecimal amount, RegisterPaymentsHalykDTO halyk,
                                                    RegisterPaymentsMerchantDTO merchant) {
        CashboxSettings percentForHalyk = cashboxSettingsService
                .getCashboxSettingByFieldNameAndCashboxId(PERCENT_PAYMENT_SYSTEM, cashBoxId);
        BigDecimal percent = new BigDecimal("0.0");
        try {
            percent = BigDecimal.valueOf(Double.parseDouble(percentForHalyk.getFieldValue()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        percent = percent.divide(new BigDecimal("100"));

        BigDecimal currentHalykMoney = halyk.getAmount() == null ? new BigDecimal("0.0") : halyk.getAmount();
        BigDecimal currentMerchantMoney = merchant.getAmount() == null ? new BigDecimal("0.0") : merchant.getAmount();

        BigDecimal moneyForHalyk = amount.multiply(percent);
        BigDecimal moneyForMerchant = amount.subtract(moneyForHalyk);

        halyk.setAmount(currentHalykMoney.add(moneyForHalyk));
        merchant.setAmount(currentMerchantMoney.add(moneyForMerchant));
    }

    private RegisterPaymentsCommonMerchantFieldsDTO getHalykCommonDataForMerchant() {
        RegisterPaymentsCommonMerchantFieldsDTO commonData = new RegisterPaymentsCommonMerchantFieldsDTO();
        HalykSettings naznpl_merch = halykSettingsRepository.findTopByFieldName(NAZNPL_MERCH);
        HalykSettings bclassd_merch = halykSettingsRepository.findTopByFieldName(BCLASSD_MERCH);
        HalykSettings kod_merch = halykSettingsRepository.findTopByFieldName(KOD_MERCH);
        HalykSettings knp_merch = halykSettingsRepository.findTopByFieldName(KNP_MERCH);
        HalykSettings rnna_merch = halykSettingsRepository.findTopByFieldName(RNNA_MERCH);
        HalykSettings platel_merch = halykSettingsRepository.findTopByFieldName(PLATEL_MERCH);
        commonData.setNaznpl_merch(naznpl_merch.getFieldValue());
        commonData.setBclassd_merch(bclassd_merch.getFieldValue());
        commonData.setKod_merch(kod_merch.getFieldValue());
        commonData.setKnp_merch(knp_merch.getFieldValue());
        commonData.setRnna_merch(rnna_merch.getFieldValue());
        commonData.setPlatel_merch(platel_merch.getFieldValue());
        return commonData;
    }

    private void setHalykCommonDataForMerchant(RegisterPaymentsMerchantDTO merchant,
                                               RegisterPaymentsCommonMerchantFieldsDTO commonData) {
        merchant.setNaznpl_merch(commonData.getNaznpl_merch());
        merchant.setBclassd_merch(commonData.getBclassd_merch());
        merchant.setKod_merch(commonData.getKod_merch());
        merchant.setKnp_merch(commonData.getKnp_merch());
        merchant.setRnna_merch(commonData.getRnna_merch());
        merchant.setPlatel_merch(commonData.getPlatel_merch());
    }

    private void setIndividualDataForMerchant(RegisterPaymentsMerchantDTO merchant,
                                              Long merchantId) {
        MerchantKyc iikInfo = merchantKycService.getMerchantKycByFieldNameAndMerchantId(IIK, merchantId);
        MerchantKyc bikInfo = merchantKycService.getMerchantKycByFieldNameAndMerchantId(BIK, merchantId);
        MerchantKyc iinbinInfo = merchantKycService.getMerchantKycByFieldNameAndMerchantId(IINBIN, merchantId);
        MerchantKyc banknameInfo = merchantKycService.getMerchantKycByFieldNameAndMerchantId(BANKNAME, merchantId);
        merchant.setIik(iikInfo.getFieldValue());
        merchant.setBik(bikInfo.getFieldValue());
        merchant.setIinbin(iinbinInfo.getFieldValue());
        merchant.setBankname(banknameInfo.getFieldValue());
    }

    private List<PaymentStatistic> getPaymentsByDate(RegisterPaymentsDateDTO registerPaymentsDateDTO) {
        long after = registerPaymentsDateDTO.getTimestampNanoSecondsAfter();
        long before = registerPaymentsDateDTO.getTimestampNanoSecondsBefore();
        return halykPaymentStatisticRepository.findAllByTimestampAfterAndTimestampBeforeAndStatus(after, before,
                "SUCCESS");
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
            number = number + 1;
            nameFile.append(number);
        } else {
            number = 1;
            nameFile.append(number);
        }
        nameFile.append(formatViewDayOrMonth(dateNow.getMonthValue()))
                .append(formatViewDayOrMonth(dateNow.getDayOfMonth()))
                .append(".txt");
        saveNumberAndDateDownloadRegister(lastDownloadsRegister.getId(), orderNumber.getId(),dateNow, number);
        return nameFile.toString();
    }

    private String formatViewDayOrMonth(int dayOrMonth) {
        return dayOrMonth < 10 ? "0" + dayOrMonth : String.valueOf(dayOrMonth);
    }

    private void saveNumberAndDateDownloadRegister(Long halykIdDate, Long halykIdNumber, LocalDateTime localDateTime, int numberOrder) {
        HalykSettings halykSettingsDate = new HalykSettings();
        halykSettingsDate.setId(halykIdDate);
        halykSettingsDate.setFieldName(DATE_LAST_DOWNLOADS);
        halykSettingsDate.setFieldValue(localDateTime.toString());
        halykSettingsRepository.save(halykSettingsDate);
        HalykSettings halykSettingsNumber = new HalykSettings();
        halykSettingsNumber.setId(halykIdNumber);
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
