package ru.vstu.adddict.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.vstu.adddict.dto.CreateTranslationRequestDto;
import ru.vstu.adddict.dto.TranslationDto;
import ru.vstu.adddict.entity.Translation;

import java.time.LocalDateTime;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        imports = {LocalDateTime.class}
)
public interface TranslationMapper {

    TranslationDto toTranslationDto(Translation savedTranslation);

    @Mapping(target = "id", ignore = true)
    Translation toTranslation(CreateTranslationRequestDto dto);
}

