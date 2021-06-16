package kz.capitalpay.server.cashbox.service;

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

    public ResultDTO getCashBoxFeeList(Principal principal) {
        try {
            ApplicationUser owner = applicationUserService.getUserByLogin(principal.getName());
            List<Cashbox> cashboxList = cashboxRepository.findByMerchantIdAndDeleted(owner.getId(), false);
            List<CashBoxFeeListDto> result = cashboxList.stream()
                            .map(o -> new CashBoxFeeListDto(o.getName(), getTotalFee(o.getMerchantId()),
                                    getClientFee(o.getId()), getMerchantFee(o.getMerchantId(), o.getId())))
                    .collect(Collectors.toList());
            return new ResultDTO(true, result, 0);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private String getClientFee(Long cashBoxId) {
        return cashboxSettingsService.getField(cashBoxId, CashboxSettingsService.CLIENT_FEE);
    }

    private String getTotalFee(Long merchantId) {
        return merchantKycService.getField(merchantId, MerchantKycService.TOTAL_FEE);
    }

    private String getMerchantFee(Long cashBoxId, Long merchantId) {
        double clientFee = getClientFee(cashBoxId).equals("") ? 0.0 : Double.parseDouble(getClientFee(cashBoxId));
        double totalFee = getTotalFee(merchantId).equals("") ? 0.0 : Double.parseDouble(getTotalFee(merchantId));
        double merchantFee = totalFee - clientFee;
        return merchantFee == 0.0 ? "0.0" : String.valueOf(merchantFee);
    }
}
