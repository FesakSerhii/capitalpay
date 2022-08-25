package kz.capitalpay.server.cashbox.service;

import kz.capitalpay.server.cashbox.dto.CashBoxFeeDto;
import kz.capitalpay.server.cashbox.dto.CashBoxFeeListDto;
import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.cashbox.repository.CashboxRepository;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.merchantsettings.service.CashboxSettingsService;
import kz.capitalpay.server.merchantsettings.service.MerchantKycService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

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
            List<Cashbox> cashboxList = cashboxRepository.findByMerchantIdAndDeletedFalse(feeDto.getMerchantId());
            List<CashBoxFeeListDto> result = cashboxList.stream().map(o -> new CashBoxFeeListDto(o.getId(), o.getName(), getTotalFee(o.getMerchantId()), getClientFee(o.getId()), getMerchantFee(o.getMerchantId(), o.getId()))).collect(Collectors.toList());
            return new ResultDTO(true, result, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO saveCashBoxFeeList(CashBoxFeeDto feeDto, Principal principal) {
        try {
//            for (CashBoxFeeListDto data : feeDto.getFeeList()) {
//                cashboxSettingsService.setField(data.getCashBoxId(), CLIENT_FEE, data.getClientFee());
//            }
            return getCashBoxFeeList(feeDto);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private String getClientFee(Long cashBoxId) {
//        String clientFee = cashboxSettingsService.getField(cashBoxId, CLIENT_FEE);
        return "0.0";
    }

    private String getTotalFee(Long merchantId) {
        return "0.0";
    }

    private String getMerchantFee(Long merchantId, Long cashBoxId) {
        double totalFee = Double.parseDouble(getTotalFee(merchantId));
        double clientFee = Double.parseDouble(getClientFee(cashBoxId));
        double merchantFee = totalFee - clientFee;
        return merchantFee == 0.0 ? "0.0" : String.valueOf(merchantFee);
    }
}
