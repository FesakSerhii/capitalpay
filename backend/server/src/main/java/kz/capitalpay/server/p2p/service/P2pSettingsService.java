package kz.capitalpay.server.p2p.service;

import kz.capitalpay.server.constants.ErrorDictionary;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.p2p.dto.P2pSettingsDto;
import kz.capitalpay.server.p2p.dto.P2pSettingsResponseDto;
import kz.capitalpay.server.p2p.model.MerchantP2pSettings;
import kz.capitalpay.server.p2p.repository.P2pSettingsRepository;
import kz.capitalpay.server.usercard.model.UserCard;
import kz.capitalpay.server.usercard.repository.UserCardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class P2pSettingsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(P2pSettingsService.class);
    private final P2pSettingsRepository p2pSettingsRepository;
    private final UserCardRepository userCardRepository;

    public P2pSettingsService(P2pSettingsRepository p2pSettingsRepository, UserCardRepository userCardRepository) {
        this.p2pSettingsRepository = p2pSettingsRepository;
        this.userCardRepository = userCardRepository;
    }

    public void createMerchantP2pSettings(Long merchantId, Long cardId) {
        MerchantP2pSettings merchantP2pSettings = new MerchantP2pSettings();
        merchantP2pSettings.setDefaultCardId(cardId);
        merchantP2pSettings.setP2pAllowed(false);
        merchantP2pSettings.setUserId(merchantId);
        p2pSettingsRepository.save(merchantP2pSettings);
    }

    public boolean existsByMerchantId(Long merchantId) {
        return p2pSettingsRepository.existsByUserId(merchantId);
    }

    public ResultDTO getP2pSettingsByMerchantId(Long merchantId) {
//        MerchantP2pSettings merchantP2pSettings = p2pSettingsRepository.findByUserId(merchantId).orElse(null);
//        P2pSettingsResponseDto dto = new P2pSettingsResponseDto();
//        if (Objects.isNull(merchantP2pSettings)) {
//            dto.setCardNumber(null);
//            dto.setMerchantId(merchantId);
//            dto.setP2pAllowed(false);
//            return new ResultDTO(true, dto, 0);
//        }
//
//        UserCard userCard = userCardRepository.findById(merchantP2pSettings.getDefaultCardId()).orElse(null);
//        dto.setCardNumber(userCard.getCardNumber());
//        dto.setMerchantId(merchantP2pSettings.getUserId());
//        dto.setP2pAllowed(merchantP2pSettings.isP2pAllowed());
//        return new ResultDTO(true, dto, 0);
        LOGGER.info("TEST OUTPUT");
        return new ResultDTO(true, "test", 0);
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
