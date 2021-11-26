package com.card.holding.repository;

import com.card.holding.model.CardData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardDataRepository extends JpaRepository<CardData, Long> {

    Optional<CardData> findByRequestValue(String requestValue);

    Optional<CardData> findByToken(String token);

}
