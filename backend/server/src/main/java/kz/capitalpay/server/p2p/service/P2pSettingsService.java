package kz.capitalpay.server.p2p.service;

import kz.capitalpay.server.constants.ErrorDictionary;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.p2p.dto.P2pSettingsDto;
import kz.capitalpay.server.p2p.model.MerchantP2pSettings;
import kz.capitalpay.server.p2p.repository.P2pSettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class P2pSettingsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(P2pSettingsService.class);
    private final P2pSettingsRepository p2pSettingsRepository;

    public P2pSettingsService(P2pSettingsRepository p2pSettingsRepository) {
        this.p2pSettingsRepository = p2pSettingsRepository;
    }

    public void createMerchantP2pSettings(Long merchantId, Long cardId) {
        MerchantP2pSettings merchantP2pSettings = new MerchantP2pSettings();
        merchantP2pSettings.setDefaultCardId(cardId);
        merchantP2pSettings.setP2pAllowed(true);
        merchantP2pSettings.setUserId(merchantId);
        p2pSettingsRepository.save(merchantP2pSettings);
    }

    public boolean existsByMerchantId(Long merchantId) {
        return p2pSettingsRepository.existsByUserId(merchantId);
    }

    public ResultDTO getP2pSettingsByMerchantId(Long merchantId) {
        Optional<MerchantP2pSettings> optionalMerchantP2pSettings = p2pSettingsRepository.findByUserId(merchantId);
        if (optionalMerchantP2pSettings.isEmpty()) {
            return ErrorDictionary.error132;
        }

        return new ResultDTO(true, optionalMerchantP2pSettings.get(), 0);
    }

    public ResultDTO setP2pSettings(P2pSettingsDto dto) {
        Optional<MerchantP2pSettings> optionalMerchantP2pSettings = p2pSettingsRepository.findByUserId(dto.getMerchantId());
        if (optionalMerchantP2pSettings.isEmpty()) {
            return ErrorDictionary.error132;
        }

        MerchantP2pSettings merchantP2pSettings = optionalMerchantP2pSettings.get();
        merchantP2pSettings.setP2pAllowed(dto.isP2pAllowed());
        merchantP2pSettings = p2pSettingsRepository.save(merchantP2pSettings);
        return new ResultDTO(true, merchantP2pSettings, 0);
    }

}
