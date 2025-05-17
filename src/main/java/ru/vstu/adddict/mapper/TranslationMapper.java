package ru.vstu.adddict.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.vstu.adddict.dto.CreateTranslationRequestDto;
import ru.vstu.adddict.dto.PageResponseDto;
import ru.vstu.adddict.dto.TranslationDto;
import ru.vstu.adddict.dto.TranslationResponseDto;
import ru.vstu.adddict.entity.Translation;

import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    TranslationResponseDto toTranslationResponseDto(TranslationDto dto);

    default <S, D> PageResponseDto<D> fromPageResponseDto(PageResponseDto<S> page, Function<S, D> map) {
        return PageResponseDto.<D>builder()
                .page(page.getPage())
                .pageSize(page.getPageSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .content(
                        page.getContent().stream()
                                .map(map)
                                .collect(Collectors.toList())
                )
                .build();
    }
}

