package com.card.holding.service;

import com.card.holding.dto.CardDataResponseDto;
import com.card.holding.dto.RegisterCardDto;
import com.card.holding.model.CardData;
import com.card.holding.repository.CardDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CardDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardDataService.class);

    private final CardDataRepository cardDataRepository;
    private final EncryptionService encryptionService;

    public CardDataService(CardDataRepository cardDataRepository, EncryptionService encryptionService) {
        this.cardDataRepository = cardDataRepository;
        this.encryptionService = encryptionService;
    }

    public ResponseEntity<?> registerCard(RegisterCardDto dto) {
        String requestValue = dto.getCardNumber() + dto.getExpireMonth() + dto.getExpireYear();
        requestValue = encryptionService.encrypt(requestValue);
        Optional<CardData> cardData = cardDataRepository.findByRequestValue(requestValue);
        if (cardData.isPresent()) {
            return ResponseEntity.ok(encryptionService.decrypt(cardData.get().getToken()));
        }

        return ResponseEntity.ok(registerNewCard(dto, requestValue));
    }

    private String registerNewCard(RegisterCardDto dto, String requestValue) {
        CardData cardData = new CardData();
        cardData.setCardNumber(encryptionService.encrypt(encryptionService.addSalt(dto.getCardNumber())));
        cardData.setCvv2Code(encryptionService.encrypt(encryptionService.addSalt(dto.getCvv2Code())));
        cardData.setExpireMonth(encryptionService.encrypt(encryptionService.addSalt(dto.getExpireMonth())));
        cardData.setExpireYear(encryptionService.encrypt(encryptionService.addSalt(dto.getExpireYear())));
        cardData.setRequestValue(requestValue);
        String token = encryptionService.generateToken();
        cardData.setToken(encryptionService.encrypt(token));
        cardDataRepository.save(cardData);
        return token;
    }

    public ResponseEntity<?> getCardData(String token) {
        Optional<CardData> optionalCardData = cardDataRepository.findByToken(encryptionService.encrypt(token));
        if (optionalCardData.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Card with such token not found!");
        }

        CardData cardData = optionalCardData.get();
        CardDataResponseDto responseDto = new CardDataResponseDto();
        responseDto.setCardNumber(encryptionService.decrypt(cardData.getCardNumber()));
        responseDto.setExpireMonth(encryptionService.decrypt(cardData.getExpireMonth()));
        responseDto.setExpireYear(encryptionService.decrypt(cardData.getExpireYear()));
        responseDto.setCvv2Code(encryptionService.decrypt(cardData.getCvv2Code()));
        responseDto.setToken(encryptionService.decrypt(cardData.getToken()));
        return ResponseEntity.ok(responseDto);
    }

}
