package com.card.holding.controller;

import com.card.holding.dto.RegisterCardDto;
import com.card.holding.service.CardDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/card-data")
public class CardDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardDataController.class);
    private final CardDataService cardDataService;

    public CardDataController(CardDataService cardDataService) {
        this.cardDataService = cardDataService;
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerCard(@Valid @RequestBody RegisterCardDto dto) {
        return cardDataService.registerCard(dto);
    }

    @GetMapping
    public ResponseEntity<?> getCardData(@RequestParam String token) {
        return cardDataService.getCardData(token);
    }

}
