package kz.capitalpay.server.p2p.service;

import kz.capitalpay.server.constants.ErrorDictionary;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.p2p.dto.MerchantTerminalSettingsDto;
import kz.capitalpay.server.p2p.dto.P2pSettingsDto;
import kz.capitalpay.server.p2p.dto.P2pSettingsResponseDto;
import kz.capitalpay.server.p2p.mapper.MerchantSettingsMapper;
import kz.capitalpay.server.p2p.model.MerchantP2pSettings;
import kz.capitalpay.server.p2p.repository.P2pSettingsRepository;
import kz.capitalpay.server.terminal.model.Terminal;
import kz.capitalpay.server.terminal.repository.TerminalRepository;
import kz.capitalpay.server.usercard.model.UserCard;
import kz.capitalpay.server.usercard.model.UserCardFromBank;
import kz.capitalpay.server.usercard.repository.UserBankCardRepository;
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
    private final UserBankCardRepository userBankCardRepository;

    private final TerminalRepository terminalRepository;
    private final MerchantSettingsMapper merchantSettingsMapper;

    public P2pSettingsService(P2pSettingsRepository p2pSettingsRepository, UserCardRepository userCardRepository, UserBankCardRepository userBankCardRepository, TerminalRepository terminalRepository, MerchantSettingsMapper merchantSettingsMapper) {
        this.p2pSettingsRepository = p2pSettingsRepository;
        this.userCardRepository = userCardRepository;
        this.userBankCardRepository = userBankCardRepository;
        this.terminalRepository = terminalRepository;
        this.merchantSettingsMapper = merchantSettingsMapper;
    }

    public MerchantP2pSettings createMerchantP2pSettings(Long merchantId, Long cardId) {
        MerchantP2pSettings merchantP2pSettings = new MerchantP2pSettings();
        merchantP2pSettings.setDefaultCardId(cardId);
        merchantP2pSettings.setP2pAllowed(false);
        merchantP2pSettings.setUserId(merchantId);
        merchantP2pSettings = p2pSettingsRepository.save(merchantP2pSettings);
        return merchantP2pSettings;
    }

    public MerchantP2pSettings createMerchantTerminalSettings(Long merchantId, Long terminalId) {
        MerchantP2pSettings merchantP2pSettings = new MerchantP2pSettings();
        merchantP2pSettings.setTerminalId(terminalId);
        merchantP2pSettings.setP2pAllowed(false);
        merchantP2pSettings.setUserId(merchantId);
        merchantP2pSettings = p2pSettingsRepository.save(merchantP2pSettings);
        return merchantP2pSettings;
    }

    public boolean existsByMerchantId(Long merchantId) {
        return p2pSettingsRepository.existsByUserId(merchantId);
    }

    public ResultDTO getP2pSettingsByMerchantId(Long merchantId) {
        MerchantP2pSettings merchantP2pSettings = p2pSettingsRepository.findByUserId(merchantId).orElse(null);
        P2pSettingsResponseDto dto = new P2pSettingsResponseDto();
        if (Objects.isNull(merchantP2pSettings)) {
            dto.setCardNumber(null);
            dto.setMerchantId(merchantId);
            dto.setP2pAllowed(false);
            return new ResultDTO(true, dto, 0);
        }

        UserCard userCard = userCardRepository.findById(merchantP2pSettings.getDefaultCardId()).orElse(null);
        if (Objects.isNull(userCard)) {
            return ErrorDictionary.CARD_NOT_FOUND;
        }
        dto.setCardNumber(userCard.getCardNumber());
        dto.setMerchantId(merchantP2pSettings.getUserId());
        dto.setP2pAllowed(merchantP2pSettings.isP2pAllowed());
        return new ResultDTO(true, dto, 0);
    }

    public ResultDTO getBankP2pSettingsByMerchantId(Long merchantId) {
        MerchantP2pSettings merchantP2pSettings = p2pSettingsRepository.findByUserId(merchantId).orElse(null);
        P2pSettingsResponseDto dto = new P2pSettingsResponseDto();
        if (Objects.isNull(merchantP2pSettings)) {
            dto.setCardNumber(null);
            dto.setMerchantId(merchantId);
            dto.setP2pAllowed(false);
            return new ResultDTO(true, dto, 0);
        }

        UserCardFromBank userCard = userBankCardRepository.findById(merchantP2pSettings.getDefaultCardId()).orElse(null);
        if (Objects.isNull(userCard)) {
            return ErrorDictionary.CARD_NOT_FOUND;
        }
        dto.setCardNumber(userCard.getCardNumber());
        dto.setMerchantId(merchantP2pSettings.getUserId());
        dto.setP2pAllowed(merchantP2pSettings.isP2pAllowed());
        return new ResultDTO(true, dto, 0);
    }

    public ResultDTO setP2pSettings(P2pSettingsDto dto) {
        Optional<MerchantP2pSettings> optionalMerchantP2pSettings = p2pSettingsRepository.findByUserId(dto.getMerchantId());
        if (optionalMerchantP2pSettings.isEmpty()) {
            return ErrorDictionary.P2P_SETTINGS_NOT_FOUND;
        }

        MerchantP2pSettings merchantP2pSettings = optionalMerchantP2pSettings.get();
        merchantP2pSettings.setP2pAllowed(dto.isP2pAllowed());
        merchantP2pSettings = p2pSettingsRepository.save(merchantP2pSettings);
        return new ResultDTO(true, merchantP2pSettings, 0);
    }

    public MerchantP2pSettings findP2pSettingsByMerchantId(Long merchantId) {
        return p2pSettingsRepository.findByUserId(merchantId).orElse(null);
    }

    public MerchantP2pSettings save(MerchantP2pSettings merchantP2pSettings) {
        return p2pSettingsRepository.save(merchantP2pSettings);
    }

    public ResultDTO setMerchantTerminalSettings(MerchantTerminalSettingsDto dto) {
        MerchantP2pSettings settings = p2pSettingsRepository.findByUserId(dto.getMerchantId()).orElse(null);
        if (Objects.isNull(settings)) {
            settings = createMerchantTerminalSettings(dto.getMerchantId(), dto.getTerminalId());
        }
        if (Objects.nonNull(settings.getTerminalId())) {
            Terminal oldTerminal = terminalRepository.findByIdAndDeletedFalse(settings.getTerminalId()).orElse(null);
            if (Objects.nonNull(oldTerminal)) {
                oldTerminal.setFree(true);
                terminalRepository.save(oldTerminal);
            }
        }
        Terminal terminal = null;
        if (Objects.nonNull(dto.getTerminalId())) {
            terminal = terminalRepository.findByIdAndDeletedFalse(dto.getTerminalId()).orElse(null);
            if (Objects.isNull(terminal)) {
                return ErrorDictionary.TERMINAL_NOT_FOUND;
            }
            if (!terminal.isFree()) {
                return ErrorDictionary.OCCUPIED_TERMINAL;
            }
            terminal.setFree(false);
            terminalRepository.save(terminal);
        }
        settings.setTerminalId(dto.getTerminalId());
        settings = p2pSettingsRepository.save(settings);
        return new ResultDTO(true, merchantSettingsMapper.toMerchantTerminalSettingsDto(settings.getUserId(), terminal), 0);
    }

    public ResultDTO getMerchantTerminalSettings(Long merchantId) {
        MerchantP2pSettings settings = p2pSettingsRepository.findByUserId(merchantId).orElse(null);
        if (Objects.isNull(settings)) {
            new ResultDTO(true, null, 0);
        }
        Terminal terminal = terminalRepository.findByIdAndDeletedFalse(settings.getTerminalId()).orElse(null);
        if (Objects.isNull(terminal)) {
            return ErrorDictionary.TERMINAL_NOT_FOUND;
        }
        return new ResultDTO(true, merchantSettingsMapper.toMerchantTerminalSettingsDto(settings.getUserId(), terminal), 0);
    }

}
