package kz.capitalpay.server.cashbox.service;

import kz.capitalpay.server.cashbox.dto.CashBoxFeeDto;
import kz.capitalpay.server.cashbox.dto.CashBoxFeeListDto;
import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.cashbox.repository.CashboxRepository;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.merchantsettings.service.CashboxSettingsService;
import kz.capitalpay.server.merchantsettings.service.MerchantKycService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import static kz.capitalpay.server.constants.ErrorDictionary.error122;
import static kz.capitalpay.server.merchantsettings.service.CashboxSettingsService.CLIENT_FEE;

@Service
public class CashboxFeeService {
    @Autowired
    CashboxRepository cashboxRepository;

    @Autowired
    ApplicationUserService applicationUserService;

    @Autowired
    CashboxSettingsService cashboxSettingsService;

    @Autowired
    MerchantKycService merchantKycService;

    public ResultDTO getCashBoxFeeList(CashBoxFeeDto feeDto) {
        try {
            List<Cashbox> cashboxList = cashboxRepository.findByMerchantIdAndDeleted(feeDto.getMerchantId(), false);
            List<CashBoxFeeListDto> result = cashboxList.stream()
                            .map(o -> new CashBoxFeeListDto(o.getId(), o.getName(), getTotalFee(o.getMerchantId()),
                                    getClientFee(o.getId()), getMerchantFee(o.getMerchantId(), o.getId())))
                    .collect(Collectors.toList());
            return new ResultDTO(true, result, 0);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO saveCashBoxFeeList(CashBoxFeeDto feeDto, Principal principal) {
        try {
            for(CashBoxFeeListDto data : feeDto.getFeeList()) {
                cashboxSettingsService.setField(data.getCashBoxId(), CLIENT_FEE, data.getClientFee());
            }
            return getCashBoxFeeList(feeDto);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private String getClientFee(Long cashBoxId) {
        String clientFee = cashboxSettingsService.getField(cashBoxId, CLIENT_FEE);
        return clientFee.equals("") ? "0.0" : clientFee;
    }

    private String getTotalFee(Long merchantId) {
        String totalFee = merchantKycService.getField(merchantId, MerchantKycService.TOTAL_FEE);
        return totalFee.equals("") ? "0.0" : totalFee;
    }

    private String getMerchantFee(Long merchantId, Long cashBoxId) {
        double totalFee = Double.parseDouble(getTotalFee(merchantId));
        double clientFee = Double.parseDouble(getClientFee(cashBoxId));
        double merchantFee = totalFee - clientFee;
        return merchantFee == 0.0 ? "0.0" : String.valueOf(merchantFee);
    }
}
