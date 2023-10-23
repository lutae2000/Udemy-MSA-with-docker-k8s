package com.example.cards.mapper;

import com.example.cards.dto.CardsDto;
import com.example.cards.entity.Cards;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CardsMapper {
    CardsMapper INSTANCE = Mappers.getMapper(CardsMapper.class);

//    @Mapping(source = "Cards.mobileNumber", target = "CardsDto.mobileNumber")
//    @Mapping(source = "Cards.cardNumber", target = "CardsDto.cardNumber")
//    @Mapping(source = "Cards.cardType", target = "CardsDto.cardType")
//    @Mapping(source = "Cards.totalLimit", target = "CardsDto.totalLimit")
//    @Mapping(source = "Cards.amountUsed", target = "CardsDto.amountUsed")
//    @Mapping(source = "Cards.availableAmount", target = "CardsDto.availableAmount")
    CardsDto mapToCardsDto(Cards cards);

//    @Mapping(source = "CardsDto.mobileNumber", target = "Cards.mobileNumber")
//    @Mapping(source = "CardsDto.cardNumber", target = "Cards.cardNumber")
//    @Mapping(source = "CardsDto.cardType", target = "Cards.cardType")
//    @Mapping(source = "CardsDto.totalLimit", target = "Cards.totalLimit")
//    @Mapping(source = "CardsDto.amountUsed", target = "Cards.amountUsed")
//    @Mapping(source = "CardsDto.availableAmount", target = "Cards.availableAmount")
    Cards mapToCards(CardsDto cardsDto);
}

/*
public class CardsMapper {

    public static CardsDto mapToCardsDto(Cards cards, CardsDto cardsDto) {
        cardsDto.setCardNumber(cards.getCardNumber());
        cardsDto.setCardType(cards.getCardType());
        cardsDto.setMobileNumber(cards.getMobileNumber());
        cardsDto.setTotalLimit(cards.getTotalLimit());
        cardsDto.setAvailableAmount(cards.getAvailableAmount());
        cardsDto.setAmountUsed(cards.getAmountUsed());
        return cardsDto;
    }

    public static Cards mapToCards(CardsDto cardsDto, Cards cards) {
        cards.setCardNumber(cardsDto.getCardNumber());
        cards.setCardType(cardsDto.getCardType());
        cards.setMobileNumber(cardsDto.getMobileNumber());
        cards.setTotalLimit(cardsDto.getTotalLimit());
        cards.setAvailableAmount(cardsDto.getAvailableAmount());
        cards.setAmountUsed(cardsDto.getAmountUsed());
        return cards;
    }

}*/
