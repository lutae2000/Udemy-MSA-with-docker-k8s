package com.example.cards.service;

import com.example.cards.constants.CardsConstants;
import com.example.cards.dto.CardsDto;
import com.example.cards.entity.Cards;
import com.example.cards.exception.CardAlreadyExistsException;
import com.example.cards.exception.ResourceNotFoundException;
import com.example.cards.mapper.CardsMapper;
import com.example.cards.repository.CardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class CardsService {

    private CardRepository cardRepository;

    public void createCard(String mobileNumber){
        Optional<Cards> optionalCards = cardRepository.findByMobileNumber(mobileNumber);
        if(optionalCards.isPresent()){
            throw new CardAlreadyExistsException("Card already registered with given mobile number " + mobileNumber);
        }
        cardRepository.save(createNewCard(mobileNumber));
    }

    private Cards createNewCard(String mobileNumber){
        long randomCardNumber = 1000000000000000L + new Random().nextLong(9000000000000000L);
        return Cards.builder()
                .cardNumber(Long.toString(randomCardNumber))
                .mobileNumber(mobileNumber)
                .cardType(CardsConstants.CREDIT_CARD)
                .totalLimit(CardsConstants.NEW_CARD_LIMIT)
                .availableAmount(CardsConstants.NEW_CARD_LIMIT)
                .amountUsed(0)
                .build();

    }

    public CardsDto searchCard(String mobileNumber){
        Cards cards = cardRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Card", "mobileNumber", mobileNumber)
        );
        return CardsMapper.INSTANCE.mapToCardsDto(cards);
        //return CardsMapper.mapToCardsDto(cards, new CardsDto());
    }

    public boolean updateCard(CardsDto cardsDto){
        Cards cards = cardRepository.findByCardNumber(cardsDto.getCardNumber()).orElseThrow(
                () -> new ResourceNotFoundException("Card", "CardNumber", cardsDto.getCardNumber())
        );
        CardsMapper.INSTANCE.mapToCards(cardsDto);
        //CardsMapper.mapToCards(cardsDto, cards);
        cardRepository.save(cards);
        return true;
    }

    public boolean deleteCard(String cardNumber){
        Cards cards = cardRepository.findByCardNumber(cardNumber).orElseThrow(
                () -> new ResourceNotFoundException("card", "cardNumber", cardNumber)
        );
        cardRepository.delete(cards);
        return true;
    }
}
