package kz.capitalpay.server.paysystems.systems.halyksoap.service;

import kz.capitalpay.server.merchantsettings.model.CashboxSettings;
import kz.capitalpay.server.merchantsettings.model.MerchantKyc;
import kz.capitalpay.server.merchantsettings.service.CashboxSettingsService;
import kz.capitalpay.server.merchantsettings.service.MerchantKycService;
import kz.capitalpay.server.paysystems.systems.halyksoap.dto.*;
import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykSettings;
import kz.capitalpay.server.paysystems.systems.halyksoap.repository.HalykRegisterPaymentsRepository;
import kz.capitalpay.server.paysystems.systems.halyksoap.repository.projection.RegisterPaymentsStatistic;
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
    private HalykSettingsService halykSettingsService;

    @Autowired
    private HalykRegisterPaymentsRepository halykRegisterPaymentsRepository;

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
        List<RegisterPaymentsStatistic> statistic = getPaymentsByDate(registerPaymentsDateDTO);
        Map<String, RegisterPaymentsMerchantDTO> register = new HashMap<>();
        RegisterPaymentsCommonMerchantFieldsDTO commonMerchantFields = getHalykCommonDataForMerchant();
        RegisterPaymentsHalykDTO halyk = new RegisterPaymentsHalykDTO();
        for (RegisterPaymentsStatistic data : statistic) {
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
        halyk.setKobd(halykSettingsService.getFieldValue(KOBD));
        halyk.setLskor(halykSettingsService.getFieldValue(LSKOR));
        halyk.setRnnb(halykSettingsService.getFieldValue(RNNB));
        halyk.setPoluch(halykSettingsService.getFieldValue(POLUCH));
        halyk.setNaznpl(halykSettingsService.getFieldValue(NAZNPL));
        halyk.setBclassd(halykSettingsService.getFieldValue(BCLASSD));
        halyk.setKod(halykSettingsService.getFieldValue(KOD));
        halyk.setKnp(halykSettingsService.getFieldValue(KNP));
        halyk.setRnna(halykSettingsService.getFieldValue(RNNA));
        halyk.setPlatel(halykSettingsService.getFieldValue(PLATEL));
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
        commonData.setNaznpl_merch(halykSettingsService.getFieldValue(NAZNPL_MERCH));
        commonData.setBclassd_merch(halykSettingsService.getFieldValue(BCLASSD_MERCH));
        commonData.setKod_merch(halykSettingsService.getFieldValue(KOD_MERCH));
        commonData.setKnp_merch(halykSettingsService.getFieldValue(KNP_MERCH));
        commonData.setRnna_merch(halykSettingsService.getFieldValue(RNNA_MERCH));
        commonData.setPlatel_merch(halykSettingsService.getFieldValue(PLATEL_MERCH));
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

    private List<RegisterPaymentsStatistic> getPaymentsByDate(RegisterPaymentsDateDTO registerPaymentsDateDTO) {
        long after = registerPaymentsDateDTO.getTimestampAfter();
        long before = registerPaymentsDateDTO.getTimestampBefore();
        return halykRegisterPaymentsRepository.findAllByTimestampAfterAndTimestampBeforeAndStatus(after, before,
                "SUCCESS");
    }

    private String createNameFile() {
        StringBuilder nameFile = new StringBuilder();
        nameFile.append(CONSTANT_NAME_REGISTER);
        HalykSettings orderNumber = halykSettingsService.getHalykSettingByFieldName(ORDER_NUMBER_REPORT);
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
        HalykSettings lastDownloadsRegister = halykSettingsService.getHalykSettingByFieldName(DATE_LAST_DOWNLOADS);
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
        halykSettingsService.saveHalykSettings(halykSettingsDate);
        HalykSettings halykSettingsNumber = new HalykSettings();
        halykSettingsNumber.setId(halykIdNumber);
        halykSettingsNumber.setFieldName(ORDER_NUMBER_REPORT);
        halykSettingsNumber.setFieldValue(String.valueOf(numberOrder));
        halykSettingsService.saveHalykSettings(halykSettingsNumber);
    }

    private void saveNumberAndDateDownloadRegister(LocalDateTime localDateTime, int numberOrder) {
        HalykSettings halykSettingsDate = new HalykSettings();
        halykSettingsDate.setFieldName(DATE_LAST_DOWNLOADS);
        halykSettingsDate.setFieldValue(localDateTime.toString());
        halykSettingsService.saveHalykSettings(halykSettingsDate);
        HalykSettings halykSettingsNumber = new HalykSettings();
        halykSettingsNumber.setFieldName(ORDER_NUMBER_REPORT);
        halykSettingsNumber.setFieldValue(String.valueOf(numberOrder));
        halykSettingsService.saveHalykSettings(halykSettingsNumber);
    }
}
