package ru.vstu.adddict.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.vstu.adddict.dto.subscribedictionary.CreateSubscribeDictionaryRequestDto;
import ru.vstu.adddict.dto.subscribedictionary.SubscribeDictionaryDto;
import ru.vstu.adddict.dto.subscribedictionary.SubscribeDictionaryResponseDto;
import ru.vstu.adddict.entity.subscribedictionary.SubscribeDictionary;

import java.time.LocalDateTime;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        imports = {LocalDateTime.class}
)
public interface SubscribeDictionaryMapper {

    @Mapping(target = "id", ignore = true)
    SubscribeDictionary toSubscribeDictionary(CreateSubscribeDictionaryRequestDto requestDto);

    SubscribeDictionaryDto toDto(SubscribeDictionary savedSubscribeDictionary);

    SubscribeDictionaryResponseDto toSubscribeDictionaryResponseDto(SubscribeDictionaryDto responseDto);
}
