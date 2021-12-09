package kz.capitalpay.server.payments.service;

import kz.capitalpay.server.cashbox.service.CashboxService;
import kz.capitalpay.server.constants.ErrorDictionary;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.payments.dto.SendP2pToClientDto;
import kz.capitalpay.server.paysystems.systems.halyksoap.service.HalykSoapService;
import kz.capitalpay.server.usercard.dto.CardDataResponseDto;
import kz.capitalpay.server.usercard.model.ClientCard;
import kz.capitalpay.server.usercard.model.UserCard;
import kz.capitalpay.server.usercard.service.UserCardService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class P2pService {

    private static final Logger LOGGER = LoggerFactory.getLogger(P2pService.class);
    private final HalykSoapService halykSoapService;
    private final CashboxService cashboxService;
    private final UserCardService userCardService;

    public P2pService(HalykSoapService halykSoapService, CashboxService cashboxService, UserCardService userCardService) {
        this.halykSoapService = halykSoapService;
        this.cashboxService = cashboxService;
        this.userCardService = userCardService;
    }

    public ResultDTO sendP2pToClient(SendP2pToClientDto dto, String userAgent, String ipAddress) {
        String secret = cashboxService.getSecret(dto.getCashBoxId());
        String sha256hex = DigestUtils.sha256Hex(dto.getCashBoxId() + dto.getMerchantId() + dto.getClientCardId() + secret);
        if (!sha256hex.equals(dto.getSignature())) {
            LOGGER.error("Cashbox ID: {}", dto.getCashBoxId());
            LOGGER.error("Merchant ID: {}", dto.getMerchantId());
            LOGGER.error("Client card ID: {}", dto.getClientCardId());
            LOGGER.error("Server sign: {}", sha256hex);
            LOGGER.error("Client sign: {}", dto.getSignature());
            return new ResultDTO(false, "Signature: SHA256(cashboxId + merchantId + clientCardId + secret)", -1);
        }

        try {
            UserCard merchantCard = userCardService.findUserCardByMerchantId(dto.getMerchantId());
            ClientCard clientCard = userCardService.findClientCardById(dto.getClientCardId());
            CardDataResponseDto merchantCardData = userCardService.getCardDataFromTokenServer(merchantCard.getToken());
            CardDataResponseDto clientCardData = userCardService.getCardDataFromTokenServer(clientCard.getToken());

            boolean paymentSuccess = halykSoapService.sendP2ToClient(ipAddress, userAgent, merchantCardData, dto, clientCardData.getCardNumber());

            return paymentSuccess ? new ResultDTO(true, "Successful payment", 0) : ErrorDictionary.error131;
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorDictionary.error130;
        }
    }
}
