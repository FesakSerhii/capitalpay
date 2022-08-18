package kz.capitalpay.server.p2p.mapper;

import kz.capitalpay.server.terminal.dto.MerchantTerminalSettingsDto;
import kz.capitalpay.server.terminal.model.Terminal;
import org.springframework.stereotype.Component;

@Component
public class MerchantSettingsMapper {

    public MerchantTerminalSettingsDto toMerchantTerminalSettingsDto(Long merchantId, Terminal terminal) {
        return new MerchantTerminalSettingsDto(
                terminal.getId(),
                terminal.getInputTerminalId(),
                terminal.getOutputTerminalId(),
                terminal.getName(),
                merchantId
        );
    }
}
